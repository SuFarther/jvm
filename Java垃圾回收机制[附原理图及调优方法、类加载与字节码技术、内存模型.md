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
#####
#####
#### 4.2 串行
#### 4.3 吞吐量优先
#### 4.4 响应时间优先
#### 4.5 G1 垃圾回收器
### 5、垃圾回收调优
#### 5.1 调优领域
#### 5.2 确定目标5.2 确定目标
#### 5.3 最快的GC是不生发GC
#### 5.4 新生代调优
#### 5.5 老年代调优
#### 5.6 案例
