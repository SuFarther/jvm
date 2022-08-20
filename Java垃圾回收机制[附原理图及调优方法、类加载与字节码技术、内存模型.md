#  Java垃圾回收机制
### 目录
### 1、如何判断对象可以回收
#### 1.1 引用计数法
&nbsp;&nbsp;&nbsp;&nbsp;每个对象有一个引用计数器，当对象被引用一次则计数器加1，当对象引用失效一次则计数器减1，
对于计数器为0的对象意味着是垃圾对象，可以被GC回收
- 引用计数法优点：实现逻辑简单
- 引用计数法缺点：无法解决循环引用问题；目前没有在使用
  ![引用计数法](https://img-blog.csdnimg.cn/20200806221117708.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl8zMjI2NTU2OQ==,size_16,color_FFFFFF,t_70)

#### 1.2 可达性分析算法
&nbsp;&nbsp;&nbsp;&nbsp;从GC Roots作为起点开始搜索，那么整个连通图中的对象便都是活对象，对于GC Roots无法到达的对象便成了垃圾回收的对象，随时可被GC回收。
- Java 虚拟机中的垃圾回收器采用**可达性分析**来探索所有存活的对象
- 扫描堆中的对象，看是否能够沿着 GC Root 对象 为起点的引用链找到该对象，找不到表示可以回收
- 可以作为GC Root的对象
  - 虚拟机栈（栈帧中的本地变量表）中引用的对象。　
  - 方法区中类静态属性引用的对象
  - 方法区中常量引用的对象
  - 本地方法栈中JNI（即一般说的Native方法）引用的对象

#### 1.3 四种引用：强、软、弱、虚引用
![四种引用：强、软、弱、虚引用](https://nyimapicture.oss-cn-beijing.aliyuncs.com/img/20200608150800.png)
##### 1.3.1 强引用
只有GC Root都不引用该对象时，才会回收强引用对象
 - 如上图B、C对象都不引用A1对象时，A1对象才会被回收
##### 1.3.2 软引用
- 仅有软引用引用该对象时，在垃圾回收后，内存仍不足时会再次触发垃圾回收，回收软引用对象(当GC Root指向软引用对象时，在内存不足时，会回收软引用所引用的对象)
  - 如上图如果B对象不再引用A2对象且内存不足时，软引用所引用的A2对象就会被回收
###### 软引用的使用
``` 
public class Demo1 {
	public static void main(String[] args) {
		final int _4M = 4*1024*1024;
		//使用软引用对象 list和SoftReference是强引用，而SoftReference和byte数组则是软引用
		List<SoftReference<byte[]>> list = new ArrayList<>();
		SoftReference<byte[]> ref= new SoftReference<>(new byte[_4M]);
	}
}
``` 
- 如果在垃圾回收时发现内存不足，在回收软引用所指向的对象时，**软引用本身不会被清理**
  - 可以配合引用队列来释放软引用自身(如果想要清理软引用，需要使用引用队列)
``` 
public class Demo1 {
	public static void main(String[] args) {
		final int _4M = 4*1024*1024;
		//使用引用队列，用于移除引用为空的软引用对象
		ReferenceQueue<byte[]> queue = new ReferenceQueue<>();
		//使用软引用对象 list和SoftReference是强引用，而SoftReference和byte数组则是软引用
		List<SoftReference<byte[]>> list = new ArrayList<>();
		SoftReference<byte[]> ref= new SoftReference<>(new byte[_4M]);

		//遍历引用队列，如果有元素，则移除
		Reference<? extends byte[]> poll = queue.poll();
		while(poll != null) {
			//引用队列不为空，则从集合中移除该元素
			list.remove(poll);
			//移动到引用队列中的下一个元素
			poll = queue.poll();
		}
	}
}
``` 
**大概思路**为：查看引用队列中有无软引用，如果有，则将该软引用从存放它的集合中移除（这里为一个list集合）

##### 1.3.3 弱引用
- 只有弱引用引用该对象时，在垃圾回收时，**无论内存是否充足**，都会回收弱引用所引用的对象
  - 如上图如果B对象不再引用A3对象，则A3对象会被回收 
- 弱引用的使用和软引用类似，只是将 SoftReference 换为了 WeakReference(可以配合引用队列来释放弱引用自身)

##### 1.3.4 虚引用
- (当虚引用对象所引用的对象被回收以后，虚引用对象就会被放入引用队列中，调用虚引用的方法)必须配合引用队列使用，主要配合ByteBuffer使用，被引用对象回收时，会将虚引用入队，由Reference Handler线程调用虚引用相关方法释放直接内存
   - 虚引用的一个体现是**释放直接内存**所分配的内存，当引用的对象ByteBuffer被垃圾回收以后，虚引用对象Cleaner就会被放入引用队列中，然后调用Cleaner的clean方法来释放直接内存
   - 如上图，B对象不再引用ByteBuffer对象，ByteBuffer就会被回收。但是直接内存中的内存还未被回收。这时需要将虚引用对象Cleaner放入引用队列中，然后调用它的clean方法来释放直接内存
##### 1.3.5 终结器引用
- (所有的类都继承自Object类，Object类有一个finalize方法。当某个对象不再被其他的对象所引用时，会先将终结器引用对象放入引用队列中，然后根据终结器引用对象找到它所引用的对象，然后调用该对象的finalize方法。调用以后，该对象就可以被垃圾回收了)无需手动编码，但其内部配合引用队列使用，在垃圾回收时，终结器引用入队（被引用对象暂时没有被回收），再由Finalizer线程通过终结器引用找到被引用对象并调用它的 finalize方法，第二次GC 时才能回收被引用对象
   -  如上图，B对象不再引用A4对象。这是终结器对象就会被放入引用队列中，引用队列会根据它，找到它所引用的对象。然后调用被引用对象的finalize方法。调用以后，该对象就可以被垃圾回收了
##### 1.3.6 引用队列
- 软引用和弱引用可以配合引用队列
  - 在弱引用和虚引用所引用的对象被回收以后，会将这些引用放入引用队列中，方便一起回收这些软/弱引用对象
- 虚引用和终结器引用必须配合引用队列
  - 虚引用和终结器引用在使用时会关联一个引用队列
    ![四种引用：强、软、弱、虚引用](https://img-blog.csdnimg.cn/20200806225631692.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl8zMjI2NTU2OQ==,size_16,color_FFFFFF,t_70)
### 2、垃圾回收算法
#### 2.1 标记清除
[//]: # (![标记清除]&#40;images/垃圾回收/20200608150813.png&#41;)
![标记清除](https://nyimapicture.oss-cn-beijing.aliyuncs.com/img/20200608150813.png)
![标记清除](https://img-blog.csdnimg.cn/202008060940251.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl8zMjI2NTU2OQ==,size_16,color_FFFFFF,t_70)
#####  **定义:** Mark Sweep
标记清除算法顾名思义，是指在虚拟机执行垃圾回收的过程中，先采用标记算法确定可回收对象，然后垃圾收集器根据标识清除相应的内容，给堆内存腾出相应的空间
 - 这里的腾出内存空间并不是将内存空间的字节清0，而是记录下这段内存的起始结束地址，下次分配内存的时候，会直接覆盖这段内存<br/>
##### **描述:** 
分为标记和清除两阶段：首先标记出所有需要回收的对象，然后统一回收所有被标记的对象。
##### **特点** 
 - 速度较快
 - 会造成内存碎片，导致在程序运行过程中需要分配较大对象的时候，无法找到足够的连续内存而不得不提前触发一次垃圾收集动作。
 - 容易产生大量的内存碎片，可能无法满足大对象的内存分配，一旦导致无法分配对象，那就会导致jvm启动gc，一旦启动gc，我们的应用程序就会暂停，这就导致应用的响应速度变慢

#### 2.2 标记整理

[//]: # (![标记整理]&#40;images/垃圾回收/20200608150827.png&#41;)
![标记整理](https://nyimapicture.oss-cn-beijing.aliyuncs.com/img/20200608150827.png)
![标记整理](https://img-blog.csdnimg.cn/20200806094219177.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl8zMjI2NTU2OQ==,size_16,color_FFFFFF,t_70)
#####  **定义:** Mark Compact
##### **描述:** 
 - 标记过程仍然与“标记-清除”算法一样，但后续步骤不是直接对可回收对象进行清理，而是<font color="#f33b45">让所有存活的对象都向一端移动，然后直接清理掉端边界以外的内存。</font>
 - 会将不被GC Root引用的对象回收，清楚其占用的内存空间。然后整理剩余的对象，可以有效避免因内存碎片而导致的问题，但是因为整体需要消耗一定的时间，所以效率较低
##### **特点** 
- 速度慢
- 没有内存碎片
#### 2.3 复制

[//]: # (![复制]&#40;images/垃圾回收/20200608150842.png&#41;)
![复制](https://nyimapicture.oss-cn-beijing.aliyuncs.com/img/20200608150842.png)

[//]: # (![复制]&#40;images/垃圾回收/20200608150856.png&#41;)
![复制](https://nyimapicture.oss-cn-beijing.aliyuncs.com/img/20200608150856.png)

[//]: # (![复制]&#40;images/垃圾回收/20200608150907.png&#41;)
![复制](https://nyimapicture.oss-cn-beijing.aliyuncs.com/img/20200608150907.png)

[//]: # (![复制]&#40;images/垃圾回收/20200608150919.png&#41;)
![复制](https://nyimapicture.oss-cn-beijing.aliyuncs.com/img/20200608150919.png)
#####  **定义:** Copy
##### **描述:**
- 将内存分为等大小的两个区域，FROM和TO（TO中为空）。先将被GC Root引用的对象从FROM放入TO中，再回收不被GC Root引用的对象。然后交换FROM和TO。这样也可以避免内存碎片的问题，但是会占用双倍的内存空间。
- 将可用内存容量划分为大小相等的两块，每次只用其中一块。当这块内存用完了，就将还存活的对象复制到另外一块上面，然后再把已使用过的内存空间一次清理掉。
   - 不会有内存碎片，但效率也不是很高
   - 需要占用双倍内存空间
##### **特点**
**优点**：自带整理功能，这样不会产生大量不连续的内存空间，适合年轻代垃圾回收。
![复制](https://img-blog.csdnimg.cn/2020080609464756.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl8zMjI2NTU2OQ==,size_16,color_FFFFFF,t_70)
### 3、分代垃圾回收
[//]: # (![分代垃圾回收]&#40;images/垃圾回收/20200608150931.png&#41;)
![分代垃圾回收](https://nyimapicture.oss-cn-beijing.aliyuncs.com/img/20200608150931.png)
当前商业虚拟机的垃圾收集都采用分代收集。此算法没啥新鲜的，就是将上述三种算法整合了一下。具体如下：
根据各个年代的特点采取最适当的收集算法：
1. 在新生代中，每次垃圾收集时候都发现有大批对象死去，只有少量存活，那就选用复制算法。只需要付出少量存活对象的复制成本就可以完成收集。
2. 老年代中因为对象存活率高、没有额外空间对他进行分配担保，就必须用标记-清除或者标记-整理。
   ![分代垃圾回收](https://img-blog.csdnimg.cn/20200806123343105.png)
  - 对象首先分配在伊甸园区域
  - 新生代空间不足时，触发minor gc，伊甸园 和 from存活的对象使用 copy 复制到 to中，存活的对象年龄加1 并且交换 from to
  - minor gc 会引发stop the word，暂停其它用户线程，等垃圾回收结束，用户线程才恢复运行
  - 当对象寿命超过阈值时，会晋升至老年代，最大寿命15 （4bit）
  - 当老年代空间不足，会先尝试触发minor gc，如果之后空间仍不足，那么触发full gc，STW的时间更长
#### 3.1 回收流程
新创建的对象都被放在了**新生代的伊甸园**中

[//]: # (![回收流程]&#40;images/垃圾回收/20200608150939.png&#41;)
![分代垃圾回收](https://nyimapicture.oss-cn-beijing.aliyuncs.com/img/20200608150946.png)
当伊甸园中的内存不足时，就会进行一次垃圾回收，这时的回收叫做 **Minor GC**
Minor GC 会将**伊甸园和幸存区FROM**存活的对象先复制到 **幸存区** TO中，
并让其**寿命加1**，再交换两个幸存区
![回收流程](https://nyimapicture.oss-cn-beijing.aliyuncs.com/img/20200608150946.png)

[//]: # (![回收流程]&#40;images/垃圾回收/20200608150946.png&#41;)
![回收流程](https://nyimapicture.oss-cn-beijing.aliyuncs.com/img/20200608150955.png)

[//]: # (![回收流程]&#40;images/垃圾回收/20200608150955.png&#41;)
[//]: # (![回收流程]&#40;images/垃圾回收/20200608151002.png&#41;)
![回收流程](https://nyimapicture.oss-cn-beijing.aliyuncs.com/img/20200608151002.png)
再次创建对象，若新生代的伊甸园又满了，则会**再次触发** Minor GC（会触发 **stop the world**， 暂停其他用户线程，只让垃圾
回收线程工作），这时不仅会回收伊甸园中的垃圾，**还会回收幸存区中的垃圾**，再将活跃对象复制到幸存区TO中。回收以后会交换两个幸存区，并让幸存区中的对象**寿命加1**
![回收流程](https://nyimapicture.oss-cn-beijing.aliyuncs.com/img/20200608151010.png)

[//]: # (![分代垃圾回收]&#40;images/垃圾回收/20200608151010.png&#41;)
如果幸存区中的对象的**寿命超过某个阈值**（最大为15，4bit），就会被放入老年代中

[//]: # (![分代垃圾回收]&#40;images/垃圾回收/20200608151018.png&#41;)
![回收流程](https://nyimapicture.oss-cn-beijing.aliyuncs.com/img/20200608151018.png)
如果新生代老年代中的内存都满了，就会先触发Minor GC，再触发Full GC，扫描新生代和老年代中所有不再使用的对象并回收

#### 3.2 GC 分析
##### 大对象处理策略
当遇到一个较大的对象时，就算新生代的伊甸园为空，也无法容纳该对象时，会将该对象直接晋升为老年代
##### 线程内存溢出
某个线程的内存溢出了而抛异常（out of memory），不会让其他的线程结束运行
这是因为当一个线程抛出OOM异常后，它所占据的内存资源会全部被释放掉，从而不会影响其他线程的运行，进程依然正常


#### 3.3 相关JVM参数
![分代垃圾回收](images/垃圾回收/相关JVM参数.png)
### 4、垃圾回收器
#### 4.1 相关概念
##### **并行收集：** 指多条垃圾收集线程并行工作，但此时用户线程仍处于等待状态。
##### **并发收集：** 指用户线程与垃圾收集线程同时工作（不一定是并行的可能会交替执行）。用户程序在继续运行，而垃圾收集程序运行在另一个CPU上
##### **吞吐量：** 即CPU用于运行用户代码的时间与CPU总消耗时间的比值（吞吐量 = 运行用户代码时间 / ( 运行用户代码时间 + 垃圾收集时间 )），也就是。例如：虚拟机共运行100分钟，垃圾收集器花掉1分钟，那么吞吐量就是99%

#### 4.2 串行
##### 描述： Serial（串行）垃圾收集器是最基本、发展历史最悠久的收集器；JDK1.3.1前是HotSpot新生代收集的唯一选择；
 - 单线程
 - 内存较小，个人电脑（CPU核数较少）
 - 针对新生代
 - 采用复制算法
 - 进行垃圾收集时，必须暂停所有工作线程，直到完成
 - 堆内存较小，适合个人电脑
``` 
-XX:+UseSerialGC = Serial + SerialOld  // -XX:+UseSerialGC   添加该参数来显示的使用串行垃圾收集器
``` 
   ![串行](https://nyimapicture.oss-cn-beijing.aliyuncs.com/img/20200608151027.png)
   ![串行](https://img-blog.csdnimg.cn/2020080619121440.png)
###### **安全点**：让其他线程都在这个点停下来，以免垃圾回收时移动对象地址，使得其他线程找不到被移动的对象
因为是串行的，所以只有一个垃圾回收线程。且在该线程执行回收工作时，其他线程进入阻塞状态
###### Serial 收集器
Serial收集器是最基本的、发展历史最悠久的收集器
特点：单线程、简单高效（与其他收集器的单线程相比），采用复制算法。
对于限定单个CPU的环境来说，Serial收集器由于没有线程交互的开销，
专心做垃圾收集自然可以获得最高的单线程手机效率。收集器进行垃圾回收时，
必须暂停其他所有的工作线程，直到它结束（Stop The World）
###### ParNew 收集器
ParNew收集器其实就是Serial收集器的多线程版本
特点：多线程、ParNew收集器默认开启的收集线程数与CPU的数量相同，
在CPU非常多的环境中，可以使用-XX:ParallelGCThreads参数来限制垃圾
收集的线程数。和Serial收集器一样存在Stop The World问题
###### **Serial Old 收集器**
Serial Old是Serial收集器的老年代版本
特点：同样是单线程收集器，采用标记-整理算法
#### 4.3 吞吐量优先
- 多线程
- 堆内存较大，多核CPU
- 单位时间内，STW（stop the world，停掉其他所有工作线程）时间最短0.2  0.2 = 0.4
- JDK1.8默认使用的垃圾回收器
``` 
-XX:+UseParallelGC    ~    -XX:+UseParallelOldGC // JDK1.8默认开启，只要开启UseParallelGC，就对应开启
-XX:+UseAdaptiveSizePolicy  // 自适应动态调整伊甸园和幸存区的内存比例
-XX:GCTimeRatio=ratio       // 目标1：1 / (1 + ratio)  一般设置ratio为19，20分钟垃圾回收不超过1分钟；会动态调整堆空间大小适应
-XX:MaxGCPauseMillis=ms     // 目标2：最大暂停用户线程时间，默认200ms
-XX:ParallelGCThreads=n     // 垃圾回收线程数                          // 垃圾回收时，CPU会飚得很高 
``` 
  ![吞吐量优先](https://img-blog.csdnimg.cn/20200806191851110.png)
  ![吞吐量优先](https://nyimapicture.oss-cn-beijing.aliyuncs.com/img/20200608151039.png)
##### Parallel Scavenge 收集器
与吞吐量关系密切，故也称为吞吐量优先收集器

**特点**：属于新生代收集器也是采用复制算法的收集器（用到了新生代的幸存区），又是并行的多线程收集器（与ParNew收集器类似）

该收集器的目标是达到一个可控制的吞吐量。还有一个值得关注的点是：GC自适应调节策略（与ParNew收集器最重要的一个区别）

**GC自适应调节策略**：Parallel Scavenge收集器可设置-XX:+UseAdptiveSizePolicy参数。当开关打开时不需要手动指定新生代的大小（-Xmn）、Eden与Survivor区的比例（-XX:SurvivorRation）、晋升老年代的对象年龄（-XX:PretenureSizeThreshold）等，虚拟机会根据系统的运行状况收集性能监控信息，动态设置这些参数以提供最优
的停顿时间和最高的吞吐量，这种调节方式称为GC的自适应调节策略。

Parallel Scavenge收集器使用两个参数控制吞吐量：
 - XX:MaxGCPauseMillis 控制最大的垃圾收集停顿时间
 - XX:GCRatio 直接设置吞吐量的大小

##### Parallel Old 收集器
是Parallel Scavenge收集器的老年代版本

**特点**：多线程，采用标记-整理算法（老年代没有幸存区）

#### 4.4 响应时间优先
- 多线程
- 堆内存较大，多核CPU
- 尽可能让单次STW时间变短（尽量不影响其他线程运行）0.1  0.1  0.1  0.1  0.1 = 0.5
```
-XX:+UseConcMarkSweepGC   ~    -XX:+UseParNewGC     ~     SerialOld
-XX:ParallelGCThreads=n   ~    -XX:ConcGCThread=threads // ParallelGCThreads为4，则ConcGCThread应该是ParallelGCThreads的1/4，对CPU占用没有Par那么高
-XX:CMSInitiatingOccupancyFraction=percent // 执行CMS执行占比，预留空间给浮动垃圾
-XX:+CMSScavengeBeforeRemark   // 在CMS垃圾标记前开启新生代垃圾回收，这样重新标记对象要少得多，Full GC时间从接近2秒，降低到300ms左右
//CMS致命问题：CMS会产生内存碎片，如果内存碎片过多，垃圾回收会退化到SerialOld单线程垃圾回收器
``` 
![响应时间优先](https://nyimapicture.oss-cn-beijing.aliyuncs.com/img/20200608151052.png)
![响应时间优先](https://img-blog.csdnimg.cn/20200806194304454.png)
##### **CMS 收集器**
Concurrent Mark Sweep，一种以获取最短回收停顿时间为目标的老年代收集器
**特点**：基于标记-清除算法实现。并发收集、低停顿，但是会产生内存碎片

**应用场景**：适用于注重服务的响应速度，希望系统停顿时间最短，给用户带来更好的体验等场景下。如web程序、b/s服务

**CMS收集器的运行过程分为下列4步：**

**初始标记：** 标记GC Roots能直接到的对象。速度很快但是仍存在Stop The World问题

**并发标记：** 进行GC Roots Tracing 的过程，找出存活对象且用户线程可并发执行

**重新标记：** 为了修正并发标记期间因用户程序继续运行而导致标记产生变动的那一部分对象的标记记录。仍然存在Stop The World问题

**并发清除：** 对标记的对象进行清除回收

CMS缺点：因为与用户工作程一起并发执行，所以会边清理，一边会产生新的垃圾

CMS收集器的内存回收过程是与用户线程一起并发执行的

**JAVA 堆垃圾回收示例：**
``` 
// GC 分析 大对象OOM
public class T01_Gc_Demo01 {
    private static final int _512KB = 512 * 1024;
    private static final int _1MB = 1024 * 1024;
    private static final int _6MB = 6 * 1024 * 1024;
    private static final int _7MB = 7 * 1024 * 1024;
    private static final int _8MB = 8 * 1024 * 1024;
 
    // -Xms20M -Xmx20M -Xmn10M -XX:+UseSerialGC -XX:+PrintGCDetails -verbose:gc
    public static void main(String[] args) throws InterruptedException {
//        ArrayList<byte[]> list = new ArrayList<>();
//        list.add(new byte[_8MB]);
//        list.add(new byte[_8MB]);
 
        // 一个线程OOM，不会导致整个进程挂掉
        new Thread(() -> {
            ArrayList<byte[]> list = new ArrayList<>();
            list.add(new byte[_8MB]);
            list.add(new byte[_8MB]);
        }, "Thread01").start();
 
        System.out.println("sleep...");
        TimeUnit.SECONDS.sleep(10);
    }
}
```
#### 4.5 G1 垃圾回收器
##### 定义：Garbage First，优先回收最有价值的垃圾区域，达到暂停时间不短的目标
  - 2004 论文发布
  - 2009JDK 6u14体验
  - 2012 JDK 7u4官方支持
  - <font color="#f33b45">2017 JDK 9默认，同时废弃了CMS垃圾回收</font>
    ![G1收集器](https://nyimapicture.oss-cn-beijing.aliyuncs.com/img/20200909201212.png)
#####  适用场景
   - 同时注重<font color="#f33b45">吞吐量（Throughput）和低延迟（Low latency）</font>，默认的暂停目标是200ms
   - <font color="#f33b45">超大堆内存</font>，会将堆划分为多个大小相等的Region (区域)
   - 整体上是标记 + 整理算法，两个区域之间是复制算法

**相关参数：** JDK8 并不是默认开启的，所需要参数开启
 - -XX:+UseG1GC
 - -XX:G1HeapRegionSize=size   // 设置Region区域大小
 - -XX:MaxGCPauseMillis=time   // 设置暂停目标，默认是200ms

总结:<font color="#f33b45">G1垃圾回收器，使用标记-整理算法，可以避免CMS标记-清除算法产生的内存碎片问题；在两个Region区域之间，则是使用复制算法。JDK8没有默认G1垃圾回收器，需要手动开启G1</font>

##### G1垃圾回收阶段
![G1垃圾回收阶段](https://img-blog.csdnimg.cn/20200807074550349.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl8zMjI2NTU2OQ==,size_16,color_FFFFFF,t_70)
- Young Collection
- Young Collection + Concurrent Mark
- Mixed Collection

新生代伊甸园垃圾回收—–>内存不足，新生代回收+并发标记—–>回收新生代伊甸园、幸存区、老年代内存——>新生代伊甸园垃圾回收(重新开始)

##### Young Collection 新生代回收
**分区算法region**
分代是按对象的生命周期划分，分区则是将堆空间划分连续几个不同小区间，每一个小区间独立回收，可以控制一次回收多少个小区间，方便控制 GC 产生的停顿时间

E：伊甸园 S：幸存区 O：老年代
- 会STW
  ![Young Collection](https://img-blog.csdnimg.cn/20200807131439125.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl8zMjI2NTU2OQ==,size_16,color_FFFFFF,t_70)
如果伊甸园进行垃圾回收，则会将伊甸园区存活的对象使用复制算法到Survivor区
  ![Young Collection](https://img-blog.csdnimg.cn/2020080809495411.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl8zMjI2NTU2OQ==,size_16,color_FFFFFF,t_70)
  当Survivor进行垃圾回收时，对象年龄超过15次，放入老年代；年龄不足15次放入另一个Survivor区域
  ![Young Collection](https://img-blog.csdnimg.cn/20200808105856818.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl8zMjI2NTU2OQ==,size_16,color_FFFFFF,t_70)

##### Young Collection + CM（新生代回收+CM）
CM: 并发标记
- 在 Young GC 时会对 GC Root 进行初始标记
- 在老年代占用堆内存的比例达到阈值时，对进行并发标记（不会STW），阈值可以根据用户来进行设定

-XX:InitiatingHeapOccupancyPercent=percent (默认45%)
![Young Collection](https://img-blog.csdnimg.cn/2020080810050520.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl8zMjI2NTU2OQ==,size_16,color_FFFFFF,t_70)

##### Mixed Collection (混合回收)
会对E、S、O进行全面垃圾回收
- <font color="#f33b45">最终标记（Remark）会STW</font>
- <font color="#f33b45">拷贝存活（Evacuation）会STW ，并不是所有老年代区域都会回收，而是回收最有价值</font>

-XX:MaxGCPauseMills:xxx 用于指定最长的停顿时间

问：为什么有的老年代被拷贝了，有的没拷贝？

因为指定了最大停顿时间，如果对所有老年代都进行回收，耗时可能过高。为了保证时间不超过设定的停顿时间，会回收最有价值的老年代（回收后，能够得到更多内存）
##### Full GC
- Serial GC
  -  新生代内存不足发生的垃圾收集 - minor gc
  -  老年代内存不足发生的垃圾收集 - full gc
- Parallel GC
  -  新生代内存不足发生的垃圾收集 - minor gc
  -  老年代内存不足发生的垃圾收集 - full gc
- CMS
  -  新生代内存不足发生的垃圾收集 - minor gc
  -  老年代内存不足，当回收速度高于垃圾产生的速度，后台不会有full gc字样
- Serial GC
  -  新生代内存不足发生的垃圾收集 - minor gc
  -  老年代内存不足，当回收速度高于垃圾产生的速度，后台不会有full gc字样
  
##### Young Collection 跨代引用
- 新生代回收的跨代引用（老年代引用新生代）问题
  - 如果遍历整个老年代根对象，显然效率会非常低；老年代设计对应一个卡表，每个卡512K，如果某个卡中的对象引用了对象，我们将此卡标记为脏卡，减少扫描范围，提升垃圾回收效率。
![Young Collection](https://img-blog.csdnimg.cn/20200808110605929.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl8zMjI2NTU2OQ==,size_16,color_FFFFFF,t_70)
- 卡表与Remembered Set
  - Remembered Set 存在于E中，用于保存新生代对象对应的脏卡
    - 脏卡：O被划分为多个区域（一个区域512K），如果该区域引用了新生代对象，则该区域被称为脏卡
- 在引用变更时通过post-write barried + dirty card queue
- concurrent refinement threads 更新 Remembered Set
![Young Collection](https://img-blog.csdnimg.cn/2020080810050520.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl8zMjI2NTU2OQ==,size_16,color_FFFFFF,t_70)

##### Remark 重标记
重新标记阶段,在垃圾回收时，收集器处理对象的过程中,黑色：已被处理，需要保留的 灰色：正在处理中的 白色：还未处理的
![Remark 重标记](https://nyimapicture.oss-cn-beijing.aliyuncs.com/img/20200608151229.png)
但是在并发标记过程中，有可能A被处理了以后未引用C，但该处理过程还未结束，在处理过程结束之前A引用了C，这时就会用到remark
过程如下
  - 之前C未被引用，这时A引用了C，就会给C加一个写屏障，写屏障的指令会被执行，将C放入一个队列当中，并将C变为 处理中 状态
  - 在并发标记阶段结束以后，重新标记阶段会STW，然后将放在该队列中的对象重新处理，发现有强引用引用它，就会处理它
![Remark 重标记](https://nyimapicture.oss-cn-beijing.aliyuncs.com/img/20200608151239.png)

##### JDK 8u20 字符串去重
过程:
- 将所有新分配的字符串（底层是char[]）放入一个队列
- 当新生代回收时，G1并发检查是否有重复的字符串
- 如果字符串的值一样，就让他们引用同一个字符串对象
- 注意，其与String.intern的区别
  - intern关注的是字符串对象
  - 字符串去重关注的是char[]
  - 在JVM内部，使用了不同的字符串标
 
缺点与缺点:
- 节省了大量内存
- 新生代回收时间略微增加，导致略微多占用CPU

-XX:+UseStringDeduplication  // 使用此功能，需要打开此配置，默认是打开
```
String s1 = new String("hello"); // char[]{'h','e','l','l','o'}
String s2 = new String("hello"); // char[]{'h','e','l','l','o'}
```
##### JDK 8u40 并发标记类卸载
所有对象都经过并发标记后，就能知道哪些类不再被使用，当一个类加载器的所有类都不再使用，则卸载它所加载的所有类
-XX:+ClassUnloadingWithConcurrentMark 默认启用
##### JDK 8u60 回收巨型对象
- 一个对象大于region的一半时，称之为巨型对象
- G1 不会对巨型对象进行拷贝
- 回收时被优先考虑
- G1 会跟踪老年代所有 incoming 引用，这样老年代incoming 引用为0的巨型对象就可以在新生代垃圾回收时处理掉

![Remark 重标记](https://img-blog.csdnimg.cn/20200808115056905.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl8zMjI2NTU2OQ==,size_16,color_FFFFFF,t_70)

##### JDK 9 并发标记起始时间的调整
- 并发标记必须在堆空间占满前完成，否则退化为Full GC （如果垃圾回收回收速度跟不上垃圾产生的速度，最终会Full GC）
- JDK9 之前需要使用 -XX:InitiatingHeapOccupancyPercent  (默认45%)
- JDK9 可以动态调整，更加合理；尽可能避免并发垃圾回收退化Full GC垃圾回收
  - -XX:InitiatingHeapOccupancyPercent 用来设置初始值
  - 进行数据采样并动态调整
  - 总会添加一个安全的空档空间
##### JDK9 更高效的回收
- 250+ 增强
- 180+bug 修复
- [https://docs.oracle.com/en/java/javase/12/gctuning]()
- G1 更加成熟、更加稳定
### 5、垃圾回收调优
预备知识
  - 掌握GC 相关的JVM参数，会基本的空间调整
    -  主要参考官网、更权威：[https://docs.oracle.com/en/java/javase/11/tools/java.html](主要参考官网、更权威：https://docs.oracle.com/en/java/javase/11/tools/java.html)
    -  查看虚拟机运行参数：java -XX:+PrintFlagsFinal -version | findstr "GC"
  - 掌握相关工具
  - 明白一点：调优跟应用、环境有关、没有放之四海纳而皆准的法则
调优原则：让长时间存活对象尽快晋升，如果长时间存活对象大量停留在新生代，新生代采用复制算法，复制来复制去，性能较低而且是个负担
查看虚拟机参数命令
```
"F:\JAVA\JDK8.0\bin\java" -XX:+PrintFlagsFinal -version | findstr "GC"
```
#### 5.1 调优领域
- 内存
- 锁竞争
- CPU占用
- IO
- GC

#### 5.2 确定目标
低延迟/高吞吐量？ 选择合适的GC
- CMS G1 ZGC
- ParallelGC
- Zing

科学运算，追求高吞吐量；互联网项目追求低延迟；高吞吐量垃圾回收，目前没有太多选择就一下ParallelGC；

低延迟垃圾回收，可以选CMS，G1， ZGC。目前互联公司还是很多在用CMS，JDK9 默认G1，不推荐CMS；因为CMS采用标记-清除算法会产生内存碎片，内存碎片多了之后会退化为serialOld，产生大幅度、长时间停顿，给用户的体验是不稳定

#### 5.3 最快的GC是不生发GC
首先排除减少因为自身编写的代码而引发的内存问题
- 查看Full GC前后的内存占用，考虑以下几个问题  
    - 数据是不是太多？
        - resultSet = statement.executeQuery("select * from 大表") ，可以加限定条数 limit n
    - 数据表示是否太臃肿
        - 对象图
        - 对象大小(java对象最小也是16字节，Integer 16字节， int 4；所以我们在选则数据类型时尽量选用基本数据类型)
    - 是否存在内存泄漏
        - 比如定义了一个静态的Map，static Map map = ，然后不停地向里面添加数据
        - 在内存紧张时，可以使用软引用
        - 在内存不足时，可以使用弱引用
        - 缓存数据时，尽量使用第三方缓存实现，比如redis/memcache，减少对堆内存依赖

#### 5.4 新生代调优
- 新生代的特点
   - 所有的new 操作的内存分配非常廉价
     - TLAB  thread-local allocation buffer，线程局部缓冲区，线程使用自己私有区域分配对象内存
   - 死亡对象的回收代价是零；因为采用复制算法，存活的对象使用复制算法到Survivor区域，剩下都是需要被回收的
   - 大部分对象用过即死，只有少数对象存活
   - Minor GC 的时间远远低于Full GC
   - 新生代优化空间更大一些
- 新生代内存越大越好么？
   -  不是
      - 新生代内存太小：频繁触发Minor GC，会STW，会使得吞吐量下降
      - 新生代内存太大：老年代内存占比有所降低，会更频繁地触发Full GC。而且触发Minor GC时，清理新生代所花费的时间会更长
   -  新生代内存设置为内容纳[并发量*(请求-响应)]的数据为宜

**幸存区调优**
   -  幸存区需要能够保存 当前活跃对象+需要晋升的对象
   - 晋升阈值配置得当，让长时间存活的对象尽快晋升

如何给新生代调优呢？是不是将新生代内存调得越大越好？下面是Oracle官方文档说明截图
网页链接：[https://docs.oracle.com/en/java/javase/11/tools/java.html#GUID-3B1CE181-CD30-4178-9602-230B800D4FAE](网页链接：https://docs.oracle.com/en/java/javase/11/tools/java.html#GUID-3B1CE181-CD30-4178-9602-230B800D4FAE)

![新生代调优](https://img-blog.csdnimg.cn/20200808142924234.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl8zMjI2NTU2OQ==,size_16,color_FFFFFF,t_70)

上述大致中文翻译：设置年轻代的堆的初始大小和最大大小（以字节为单位）。 字母k或K表示千字节，m或M表示兆字节，g或G表示千兆字节。 堆的年轻代区域用于新对象。 与其他区域相比，在该区域执行GC的频率更高。 如果年轻代设置太小，则会执行大量 minor gc垃圾回收。 如果设置太大，则仅执行full gc垃圾回收才有效，这可能需要很长时间才能完成。 Oracle官方建议设置年轻代的大小保持大于堆总大小的25％，并且小于堆总大小的50％。
<hr/>
<font color="#f33b45">总结：</font> 新生代，还是需要调大一些，因为新生代采用复制算法，需要移动对象，复制算法性能效率较低。

<font color="#f33b45">公式：</font> 新生代能容纳所有【并发量 * （请求 - 响应）】的数据
 - 幸存区大到能保留【当前活跃对象 + 需要晋升对象】，原则就是让真正需要进入老年代的对象才进入老年代。
 - 晋升阈值配置得当，让长时间存活对象尽快晋升
   -XX:MaxTenuringThreshold=threshold     //  设置年龄阈值，大值为15。并行（吞吐量）收集器的默认值为15，而CMS收集器的默认值为6。
   -XX:+PrintTenuringDistribution                  //  启用打印保有权年龄信息，这个参数对于设置-XX:MaxTenuringThreshold有很大帮助，阀值需要长时间观察对象分布，设置合理即可。
#### 5.5 老年代调优
以CMS 为例
  - CMS的老年代内存越大越好
  - 先尝试不做调优，如果没有Full GC 那么老年代已经足够大了；如果有Full GC 则先尝试调优新生代
  - 观察发生Full GC 时老年代内存占用，将老年代内存预设调大 1/4  ~  1/3  
    - -XX:CMSInitiatingOccupancyFraction=percent    // 控制老年代占用空间大小占总空间大小比例，进行CMS垃圾回收；值越小就越早进行垃圾回收，推特工程师有一个演讲建议将此值设置为0，即一有垃圾就回收；一般我们将此值设置75%~80%之间，预留25%-20%给浮动垃圾

#### 5.6 案例
  - 案例1：Full GC 和Minor GC频繁(一分钟上百次)，意味着堆内存空间紧张，可能是新生代空间过小，导致不需要晋升到老年代的对象进入老年代，然后老年代空间存在大量这种对象，空间也紧张就是频繁gc；
  - 案例2：请求高峰期发生Full GC，单次暂停时间特别长（CMS）；可以重新标记前开启垃圾回收，这样重新标记对象数没有那多，性能有一定提高；
  - 案例3：老年代充裕情况下，发生Full GC (1.7) ；可能是JDK1.7永久代空间不足导致内存不足；JDK1.8元空间使用系统内存不易内存溢出案例3：老年代充裕情况下，发生Full GC (1.7) ；可能是JDK1.7永久代空间不足导致内存不足；JDK1.8元空间使用系统内存不易内存溢出
