#Synchronized 
## synchronized 简介
Java 中提供了两种实现同步的基础语义：synchronized 和 synchronized 块 ，
    
    public class SyncTest {
    
        public void syncBlock(){
            synchronized(this){
                System.out.println("hello block");
            }
        }
    
        public synchronized void syncMethod(){
    
            System.out.println("hello method");
        }
    }

当 SyncTest.java被编译成 class 文件的时候，synchronized 关键字和synchronized方法的字节码略有不同，我们可以用javap -v 命令查看class
文件对应的JVM 字节码信息，如下：
    
    {
      public void syncBlock();
        descriptor: ()V
        flags: ACC_PUBLIC
        Code:
          stack=2, locals=3, args_size=1
             0: aload_0
             1: dup
             2: astore_1
             3: monitorenter                      // monitorenter指令进入同步块
             4: getstatic     #2                  // Field java/lang/System.out:Ljava/io/PrintStream;
             7: ldc           #3                  // String hello block
             9: invokevirtual #4                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
            12: aload_1
            13: monitorexit                          // monitorexit指令退出同步块
            14: goto          22
            17: astore_2
            18: aload_1
            19: monitorexit                          // monitorexit指令退出同步块
            20: aload_2
            21: athrow
            22: return
          Exception table:
             from    to  target type
                 4    14    17   any
                17    20    17   any
    
    
      public synchronized void syncMethod();
        descriptor: ()V
        flags: ACC_PUBLIC, ACC_SYNCHRONIZED      //添加了ACC_SYNCHRONIZED标记
        Code:
          stack=2, locals=1, args_size=1
             0: getstatic     #2                  // Field java/lang/System.out:Ljava/io/PrintStream;
             3: ldc           #5                  // String hello method
             5: invokevirtual #4                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
             8: return
    
    }
    
对于synchronized 关键字而言，javac在编译时，会生成对应的monitorenter 和 monitorexit 指令分别对应synchronized 同步块的进入和退出，有两个
monitorexit指令的原因时：为了保证抛异常的情况下也能释放锁，所以javac 为同步代码块添加一个隐式的 try-finally ，在finally 中会调用 monitorexit
命令释放锁，而对于synchronized方法而言，javac 为其生成了一个 ACCSYNCHRONIZED 关键字，在JVM进行方法调用时，发现调用的方法被ASSUYNCCHRONIZED修饰
则会先尝试获得锁。  

在JVM底层，对于这两种synchronized 语义的实现大致相同。  

##  锁的几种形式
传统的锁（也就是下文中 要说的重量级锁）依赖于系统的同步函数，在Linux 上使用 mutex 互斥锁，最底层实现依赖于futex.。这些同步函数涉及到用户态和内核态
的切换、进程上下文切换、成本较高、对于加了synchronized关键字但是运行时并没有多线程竞争，或两线程接近于交替执行的情况，使用传统锁机制无疑效率是会比较
低的。  

在JDK1.6之前，synchronized 只有传统的锁机制，因此给开发者留下了synchronized关键字相比于其他同步机制性能不好的印象  

在JDK1.6之后，引入了两种新型锁机制：偏向锁和轻量级锁，他们的引入是为了解决在没有多线程的竞争或基本没有竞争的场景下因使用传统锁机制带来的性能开销问题。 

### 对象头
因为Java中任意对象都可以用作锁，因此必定要有一个映射关系，存储该对象以及其对应的锁信息（比如当前那个线程尺有所，哪些线程在等待）。一种直观的方法是，
用一个全局的Map,来存储这个映射关系，但是这样会有一些问题：需要对MAP做线程安全保障。不同的synchronized 之间会相互影响，性能差；另外当同步对象较多时，
该map可能会占用比较多的内存。  


所以组好的办法就是将这个映射关系存储在对象头中，因为对象头本身也有一些hashCode，GC相关的数据，所以如果能将锁信息与这些信息共同存在对象头中就好了  

在JVM中，对象在内存中除了本身的数据以外还有个对象头，mark word 用于存储对象的hashCode、GC分代年龄、锁状态等信息。在32位系统上mark-world 长度为32
字节、64为系统上长度为64字节，为了能在有限的空间里存储下更多的数据，其存储格式是不固定的，在32为系统上各个状态的格式如下：

可以看到锁信息也是存在于对象的mark word 中的，当对象状态为偏向锁时，mark word存储的是偏向的线程ID，当状态为轻量级锁是，mark word 存储的是指向线程
栈中Lock Record 的指针。当状态为重量级时，为指向堆中的monitor对象的指针。  

##重量级锁  
重量级锁是我们常说的传统意义上的锁，其利用操作系统底层的同步机制取实现 java 中的线程同步。  
重量级锁的状态下，对象的mark word 为指向一个堆中monitor对象的指针。  
一个monitor对象头包括这么几个关键字段：cxq EntryList WaitSet owner
其中 cxq ，EntryList \ WaitSet 都是由ObjectWaiter的链表结构，owner 指向持有锁的线程。  

当一个线程尝试获得锁时，如果该锁已经被占用，则会被该线程封装成一个ObjectWaiter 对象插入到cxq 的队列尾部，然后暂停当前线程，当持有锁的线程释放前，会将
cxq 中的所有元素移动到EntryList中，并唤醒EntryList的队首线程。  

如果一个线程在同步块中调用了Object#wait 方法，会将该线程对应的ObejctWaiter 从EntryList移除并加入到WaitSet中，然后释放锁，当wait的线程被notify
后，会将对应的ObjectWaiter从WaitSet移动到EntryList中。  

## 轻量级锁
JVM的开发者发现在很多情况下，在JAVA程序运行时，同步块中的代码都是不存在竞争的，不同的线程交替的执行同步块中的代码。这种情况下，用重量级锁时没有必要的
因此JVM引入了轻量级锁的概念。  

线程在执行同步块之前，JVM会先在当前的线程的栈帧中创建一个Lock Record ，其包括一个用于存储对象头中的mark word 以及一个指向对象的指针。

### 加锁过程
1、在线程栈中创建一个 Lock Record ，其obj（Object reference）字段指向锁对象。  
2、直接通过CAS指令将Lock Record 的地址存储在对象头的mark word 中，如果对象处于无所状态则修改成功，代表该线程获得了轻量级锁。如果失败，进入步骤3
3、如果当前线程已经持有该锁了，代表这是一个锁重入，设置Lock Record 第一部分为null，起到了一个重入计数器作用，然后结束。  
4、走到这一步说明发生了竞争，需要膨胀为重量级锁。  

### 解锁过程  
1 遍历线程栈，找到所有obj字段等于当前锁对象的Lock Record。
2 如果Lock Record 的Displaced Mark World 为null，代表这是一个重入，将obj设置为null后continue；  
3 如果Lock Record 的Displaced Mark world 不为null,则利用CAS指令将对象头的mark world 恢复成为Displaced Mark World。如果成功，则continue，否则膨胀
为重量级锁。

### 偏向锁  
Java 是支持多线程的语言，因此在很多二方包、基础库中为了保证代码在多线程的情况下也能正常运行，也就是我们常说的线程安全，都会加入如synchronized
这样的同步语义。但是在应用在实际运行时，很可能只有一个线程调用相关同步方法。

    public class SyncDemo1 {

        public static void main() {
            SyncDemo1 syncDemo1 = new SyncDemo1();
            for (int i = 0; i < 100; i++) {
                syncDemo1.addString("test" + i);
            }
        }
        private List<String> list = new ArrayList<String>();
        public synchronized void addString(String s){
            list.add(s);
        }
    }

在这个demo中为了保证堆list操纵时线程安全，对addString方法加入了synchronized 的修饰，但实际使用时却只有一个线程调用该方法，对于轻量级锁而言，每次
调用addString 时，加锁和解锁都有一个CAS操作，对于重量级锁而言，加锁也会有一个或者多个CAS操作  
在JDK1.6中为了提高一个对象在一段很长的时间内都被一个线程用做锁对象场景下的性能，引入了偏向锁，在第一次获得锁时，会有一个CAS操作，之后改线程再
获取锁，只会执行几个简单的命令，而不是开销相对较大的CAS命令。我们来看看偏行锁是如何创建的

### 对象创建  
当JVM启用了偏向锁模式，当新创建一个对象的时候，如果该对象所属的class 没有关闭偏向锁模式，那么新创建对象的mark word 将是可偏向状态，此时mark word
中的thread id 为0 ，表示未偏向任何线程，也叫做匿名偏向。  

### 加锁过程  
case1:当该对象第一次被线程获得锁的时候，发现匿名偏向状态，则会用CAS指令，将Mark word中的thread ID 由0 改成当前线程ID，如果成功，则代表获得了偏向
锁，继续执行同步块中的代码，否则，将偏向锁撤销，升级为轻量级锁。  
case2:当被偏向的线程再次进入同步块中，发现锁对象偏向的就是当前线程，再通过一些额外的检查后，会往当前线程的栈中添加一条Displaced Mark Word为空的
Lock Record中，然后秩序执行同步块的代码，因为操纵的是线程私有的栈，因此不需要用到CAS指令；由此可见偏向锁模式下，当被偏向的线程再次尝试获得锁时，
仅仅进行几个简单的操作就可以了，在这种情况下，synchronized 关键字带来的性能开销基本可以忽略。  

case3:当其他线程进入同步块时，发现已经由偏向的线程，则会进入到撤销偏向锁的逻辑里，一般来说，会在safepoint中查看偏向的线程是否还 存活，如果存活
且还再同步块中则将锁升级为轻量级锁，原偏向的线程继续拥有锁，当前线程则走入到锁升级的逻辑里。如果偏向的线程已经不存活，或者不再同步块中，则将
对象头的mark word 改为无锁状态，之后再升级为轻量级锁。  

### 解锁过程  
当有其他线程尝试获得锁时，时根据遍历偏向线程的lock record 来确定该线程是否还在执行同步块中的代码。因此偏向锁的解锁很简单，仅仅将栈中的最近一条lock record
的obj字段设置为null，需要注意的是，偏向锁的结果步骤中并不会修改对象头的thread id。

另外，偏向锁默认不是立即就启动的，再程序启动后，通常由几秒的延迟，可以通过 -XX:BiasedLockingStartupDelay = 0 来关闭延迟。  

### 批量重偏向与撤销  
从上问偏向锁的加锁解锁过程中可以看出，当只有一个线程反复进入同步块时，偏向锁带来的性能开销基本可以忽略，但是当有其他线程尝试获得锁时，就需要等待
safe point 时偏向锁撤销为无锁状态或升级为轻量级/重量级锁，safe point 这个词我们再GC中经常会提到，其代表了一个状态，再该状态下所有线程都是暂停的
。总之，偏向锁的撤销是有一定成本的，如果说运行时的场景本身存在多线程竞争的，那偏向锁的存在不仅不能提高性能，而且会导致性能下降。因此JVM中增加
了一种批量重偏向/插销的机制。  

1 一个线程创建了大量杜希艾姑娘并执行了初始化的同步操作，之后再另一个线程中将这些对象作为锁进行之后的操作，这种case下，会导致大量的偏向锁撤销操作。  
2 存在明显多线程竞争的场景下使用偏向锁时不合适的，例如生产者/消费者队列。批量重偏向机制是为了解决第一种场景，批量撤销则是为了解决第二种场景。  
其做法是：以class为单位，为每个class维护一个偏向锁撤销计数器，每一次该class的对象发生偏向撤销操作时，该计数器+1，当这个值达到重偏向阈值（默认20）
时，JVM就认为该class 的偏向锁有问题。因此会进行批量重偏向，每个class 对class对象会有一个对象的epoch字段，每个处于偏向锁状态对象的mark word中
也有该字段，其初始值为创建该对象时，class 中epoch的值，每次发生批量重偏向时，就将该值+1，同时遍历JVM 中的所有线程的栈，找到该class 所有正处于
加锁状态的偏向锁，将其epoch字段改为新值。下次获得锁时，发现当前对喜爱给你的epoch值class的epoch不相等，那就算当前已经偏向了其他线程，也不会执行
插销操作，而是直接通过 CAS 操作将其mark word的Thread id 改成当前线程的ID。  

当达到重偏向阈值后，假设该class 计数器继续增长，当其达到批量撤销的阈值后（默认40），JVM就认为该class 的使用场景存在多线程竞争，会标记该class
为不可偏向，之后，对于该class的锁，直接走轻量级锁的逻辑。  

### END
Java 中的synchronized 有偏向锁、轻量级锁、重量级锁三种形式，分别对应了锁只被一个新线程持有、不同线程交替持有锁、多线程竞争锁三种情况。当条件
不满足时，锁会被偏向锁》轻量级锁》重量级锁的顺序升级。JVM中的锁也是能够降级的，只不过条件很苛刻。