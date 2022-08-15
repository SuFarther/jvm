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
<p style="font-size: 16px"><font style="font-weight: bold">作用:</font> 是记住下一条jvm指令的执行地址</p>

<p style="font-size: 16px"><font style="font-weight: bold">特点:</font> <font color="#f33b45">线程私有的； 不存在内存溢出，也是JVM规范中唯一没有OutOfMemoryError的区域</font></p>

<p style="font-size: 16px"><font style="font-weight: bold">二进制字节码: </font> JVM指令 —>  解释器  —> 机器码 —> CPU</p>

<p style="font-size: 16px"><font style="font-weight: bold">程序计数器：</font> 记住下一条jvm指令的执行地址，硬件方面通过【寄存器】实现</p>

示例： 二进制字节码：jvm指令       java 源代码
![二进制字节码](https://img-blog.csdnimg.cn/20200730203706398.png)

