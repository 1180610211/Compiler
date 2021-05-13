该项目采用Maven进行依赖管理，引用了外部的Jar包，因此请确保采用Maven项目打开。

建议采用IDEA打开，运行Main类中的main方法，即可启动GUI界面，根据菜单栏选择所要进行的操作。


依赖如下：
```xml
<dependencies>
        <!-- https://mvnrepository.com/artifact/org.fxmisc.richtext/richtextfx -->
        <dependency>
            <groupId>org.fxmisc.richtext</groupId>
            <artifactId>richtextfx</artifactId>
            <version>0.10.0</version>
        </dependency>
</dependencies>
```

## 词法分析
![img1.png](./img/img1.png)

## 语法分析
文法保存在src/main/resources/grammar.txt，具体内容如下：
``` 
S ::= Program
Program ::= ExtDefList
ExtDefList ::= ExtDefList ExtDef | ε
ExtDef ::= Type VarDec ; | struct ID { DefList } ; | function Type FunDec CompSt | function Type FunDec ; 

BaseType ::= bool | char | int | float | double
Type ::= BaseType | struct ID

DefList ::= DefList Def | ε
Def ::= Type VarDec ; | struct ID { DefList } ;

VarDec ::= ID | VarDec [ INTNUM ]
FunDec ::= ID ( VarList ) | ID ( )
VarList ::= VarList , ParamDec | ParamDec
ParamDec ::= Type VarDec

CompSt ::= { DefList StmtList }
StmtList ::= StmtList Stmt | ε

Stmt ::= Exp ; | ID = Exp ; | L = Exp ; | CompSt | if ( B ) Stmt | if ( B ) Stmt else Stmt | return Exp ; | while ( B ) Stmt | do Stmt while ( B ) ; 
L ::= ID [ Exp ] | L [ Exp ]
Exp ::= Exp + Exp | Exp - Exp | Exp * Exp | Exp / Exp | ( Exp ) | ID | L | INTNUM | REALNUM | STRING | call ID ( ) | call ID ( ParamList )
ParamList ::= ParamList , Exp | Exp
B ::= B || B | B && B | ! B | ( B ) | Exp relop Exp | true | false
relop ::= < | <= | == | != | > | >=
```

![img2.png](./img/img2.png)
![img3.png](./img/img3.png)

语法分析如果没有错误则打印对应的语法分析树，存在错误则采用恐慌模式进行恢复，并输出相应的错误信息。

## 语义分析
语法分析修改后文法保存在src/main/resources/grammar.txt，增添了对于结构体的成员运算符的操作，具体内容如下：
``` 
S ::= Program
Program ::= ExtDefList
ExtDefList ::= ExtDefList ExtDef | ε
ExtDef ::= Def | struct ID { O DefList } ; | function Type FunDec { O P DefList StmtList } | function Type FunDec ; 

BaseType ::= bool | char | int | float | double
Type ::= BaseType | struct ID

DefList ::= DefList Def | ε
Def ::= Type ID dims ; 

dims ::= ε | [ INTNUM ] dims
FunDec ::= ID ( VarList ) | ID ( )
VarList ::= VarList , ParamDec | ParamDec
ParamDec ::= Type ID dims

CompSt ::= { O DefList StmtList }
StmtList ::= StmtList M Stmt | ε

Stmt ::= Exp ; | ID = Exp ; | L = Exp ; | CompSt | if ( B ) M Stmt | if ( B ) M Stmt N else M Stmt | return Exp ; | while M ( B ) M Stmt | do M Stmt while ( M B ) ; | Q = Exp ;
L ::= ID [ Exp ] | L [ Exp ] 
Q ::= Exp . ID
Exp ::= Exp + Exp | Exp - Exp | Exp * Exp | Exp / Exp | ( Exp ) | ID | L | INTNUM | REALNUM | call ID ( ) | call ID ( ParamList ) | Q
ParamList ::= ParamList , Exp | Exp
B ::= B || M B | B && M B | ! B | ( B ) | Exp relop Exp | true | false 
relop ::= < | <= | == | != | > | >=

M ::= ε
N ::= ε
O ::= ε 
P ::= ε 
```
### 中间代码生成
![img4.png](./img/img4.png)

### 符号表
![img5.png](./img/img5.png)

### 错误处理
![img5.png](./img/img6.png)
测试用例覆盖以下类型的错误：
* 变量（包括数组、指针、结构体）或过程未经声明就使用 
  
* 变量（包括数组、指针、结构体）或过程名重复声明

* 运算分量类型不匹配（也包括赋值号两边的表达式类型不匹配）

* 操作符与操作数之间的类型不匹配
  
* 赋值号左边出现一个只有右值的表达式

* 数组下标不是整数

* 对非数组变量使用数组访问操作符

* 对非结构体类型变量使用“.”操作符

* 对非过程名使用过程调用操作符

* 过程调用的参数类型或数目不匹配