# 深入JAVA 的JVM核心原理解决线上各种故障
## 目录
### 什么是JVM?
Java Virtual Machine - java程序的运行环境（java二进制字节码的运行环境）

### JVM好处？
1. 一次编写，到处运行的基石【重点】
2. 自动内存管理，垃圾回收功能【重点】
3. 数据下标越界检查
4. 多态，面向对象编程

### JVM、JRE、JDK三者比较：
![JVM、JRE、JDK三者比较](https://img-blog.csdnimg.cn/20200730200026124.png)

### 学习JVM有什么用？
```
1、面试
2、理解底层的实现原理
3、中高级程序员的必备技能
```

### JVM组成有哪些？
![JVM组成有哪些？1](https://img-blog.csdnimg.cn/20200801150851388.png)
![JVM组成有哪些？2](https://img-blog.csdnimg.cn/20200730200218485.png)

### 常见的JVM
![常见的JVM](https://img-blog.csdnimg.cn/20200730200140764.png)
### JAVA 内存结构组成
1. 程序计时器
2. 虚拟机栈
3. 本地方法栈
4. 堆
5. 方法区

<hr/>

### 1、程序计数器
![常见的JVM](https://img-blog.csdnimg.cn/2020073020344595.png)

#### 1.1 程序计数器定义
 > Program Counter Register程序计数器（寄存器）

#### 1.2 程序计数器作用
<font size="3" >**作用:** 是记住下一条jvm指令的执行地址</font>

<font size="3" >**特点:** <font color="#f33b45">线程私有的； 不存在内存溢出，也是JVM规范中唯一没有OutOfMemoryError的区域</font></font>

<font size="3" >**二进制字节码:** JVM指令 —>  解释器  —> 机器码 —> CPU</font>

<font size="3" >**程序计数器**  记住下一条jvm指令的执行地址，硬件方面通过【寄存器】实现</font>

示例： 二进制字节码：jvm指令       java 源代码
![二进制字节码](https://img-blog.csdnimg.cn/20200730203706398.png)

<hr/>

### 2、虚拟机栈（-Xss256k）
![虚拟机栈](https://img-blog.csdnimg.cn/20200730203900544.png)

先了解一程数据结构
- 栈Stack，先进后出FILO
- 栈-线程运行需要的内存空间
- 栈帧-每个方法运行时需要的内存

#### 2.1 栈定义
Java Virtual Machine Stacks (Java虚拟机栈)
1. 每个线程运行时所需要的内存，称为虚拟机栈
2. 每个栈由多个栈帧（Frame）组成，对应着每次方法调用时所占用的内存
3. 每个线程只能有一个活动栈帧，对应着当前正在执行的那个方法

#### 2.2 栈问题
1. 垃圾回收是否涉及栈内存？   答案：栈内存不涉及垃圾回收
2. 栈内存分配越大越好吗？ 答案：栈内存不是越大越好，如果设置过大，会影响可用线程的数量；比如-Xss1m、-Xss2m，在总内存不变的情况下，可用线程数量会减少
3. 方法内的局部变量是否线程安全？   答案：方法内的局部变量是线程安全，因为方法内的局部变量各自在自已独立的内存中；如果是static int 就是线程共享的，就不是线程安全；主要看变量是否是线程共享、还是线程私有

核心1：<font color="#f33b45" size="3">如果方法内局部变量没有逃离方法的作用范围，它是线程安全的</font><br/>
核心2：<font color="#f33b45" size="3">如果是局部变量引用了对象，并逃离方法的作用范围，需要考虑线程安全</font><br/>

#### 2.3 栈内存溢出（-Xss256k）
栈帧过多导致栈内存溢出，比如：递归，我们生产环境推荐尽量不使用递归<br/>
栈帧过大导致栈内存溢出

#### 线程运行诊断（附案例）
##### 2.3.1 cpu占用过高，如何诊断案例 
<font color="#f33b45" size="3">1. 用top定位哪个进程对cpu的占用过高</font>
![用top定位哪个进程对cpu的占用过高](https://img-blog.csdnimg.cn/20200730204732769.png)
<font color="#f33b45" size="3">2. ps H -eo pid,tid,%cpu     查看linux所有进程、线程、CPU消耗情况</font>
![ 查看linux所有进程、线程、CPU消耗情况](https://img-blog.csdnimg.cn/20200730204847966.png)
<font color="#f33b45" size="3">3. ps H -eo pid,tid,%cpu | grep 进程id    用ps命令进一步定位哪个线程引起的CPU占用过高</font>
![用ps命令进一步定位哪个线程引起的CPU占用过高](https://img-blog.csdnimg.cn/20200730204926135.png)
<font color="#f33b45" size="3">4. jstack 进程pid        需要将十进制的线程id转成16进制; 可以根据线程id找到有问题的线程，进一步定位问题代码的源码行号</font>
![需要将十进制的线程id转成16进制; 可以根据线程id找到有问题的线程，进一步定位问题代码的源码行号](https://img-blog.csdnimg.cn/20200730205019222.png)
<font color="#f33b45" size="3">通过上述方式找到了源代码CPU消耗过高的文件及行号</font>
![CPU消耗过高的文件及行号](https://img-blog.csdnimg.cn/2020073020511734.png)

##### 3.2 程序运行很长时间没有结果，如何诊断案例
``` nohup java -cp /root/JvmLearn-1.0-SNAPSHOT.jar com.jvm.stack.T07_StackDeadLock & ``` 

![程序运行很长时间没有结果，如何诊断案例](https://img-blog.csdnimg.cn/20200730205246527.png)
![程序运行很长时间没有结果，如何诊断案例](https://img-blog.csdnimg.cn/20200730205339226.png)

<hr/>
