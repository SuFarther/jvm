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

##### 2.3.2 程序运行很长时间没有结果，如何诊断案例
``` nohup java -cp /root/JvmLearn-1.0-SNAPSHOT.jar com.jvm.stack.T07_StackDeadLock & ``` 

![程序运行很长时间没有结果，如何诊断案例](https://img-blog.csdnimg.cn/20200730205246527.png)
![程序运行很长时间没有结果，如何诊断案例](https://img-blog.csdnimg.cn/20200730205339226.png)

<hr/>

### 3、本地方法栈（不是Java编写的代码，通过C/C++）


### 4、堆（-Xmx8m）
#### 4.1 堆的定义
#### 4.2 堆内存溢出问题及生产建议
#### 4.3 堆内存诊断工具介绍，及实操
##### 4.3.1 垃圾回收后，内存占用仍然很高，排查方式案例

### 5、元空间/方法区（-XX:MaxMetaspaceSize=8m）
#### 5.1 JVM方法区定义
#### 5.2 方法区组成
#### 5.3 方法区内存溢出
##### 5.3.1 元空间内存溢出演示案例
##### 5.3.2 生产环境出现元空间内存溢出问题，应该锁定这些方面
#### 5.4 运行时常量池
##### 5.4.1 字符串常量池JVM字节码方面原理演示
#### 5.5 StringTable
##### 5.5.1 StringTable常量池与串池的关系
#### 5.6 StringTable特性
#### 5.7 StringTable位置
##### 5.7.1 JDK1.8 字符串常量池在堆中实例验证
#### 5.8 StringTable垃圾回收
#### 5.9 StringTable 性能调优（案例）
##### 5.9.1 使用-XX:StringTableSize=大小参数增加桶的数量使StringTable性能增加案例
##### 5.9.2 使用字符串常量池对字符串较多的场景减少内存占用案例

### 6、直接内存Direct Memory
#### 6.1 直接内存定义
#### 6.2 原理讲解
#### 6.3 直接内存与传统方式读取大文件耗时对比案例
#### 6.4 直接内存溢出案例
#### 6.5 分配和使用原理
#### 6.6 分配和回收原理及案例演示
