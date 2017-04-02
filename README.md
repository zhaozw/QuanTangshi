## Android程序《离线全唐诗》

设计目的：利用碎片化时间，无障碍欣赏唐诗。  
下载编译好的.apk安装文件：https://pan.baidu.com/s/1kVhtnl9

### 特性
1.  离线全唐诗数据库，有5万余首唐诗。  
2.  支持繁体、简体切换。  
3.  标签功能，可以给诗打标签，并通过标签进行检索。  
4.  辅助学习功能，点击几下就可以搜索唐诗里的古代词汇、典故，让您无障碍欣赏唐诗。  
5.  开放源代码。

### 截图
<table>
<tr>
<td>阅读</td><td>学习功能</td><td>标签功能</td><td>最近列表</td>
</tr>
<td><img src="https://raw.githubusercontent.com/animalize/pics/master/QuanTangshi/1.png" /></td>
<td><img src="https://raw.githubusercontent.com/animalize/pics/master/QuanTangshi/2.png" /></td>
<td><img src="https://raw.githubusercontent.com/animalize/pics/master/QuanTangshi/3.png" /></td>
<td><img src="https://raw.githubusercontent.com/animalize/pics/master/QuanTangshi/4.png" /></td>
</table>

### 编译指南
需要生成唐诗的SQLite数据库文件，步骤如下：
1.  下载原始的[全唐诗数据](https://github.com/jackeyGao/chinese-poetry)，此为json格式。
2.  给电脑安装Python 3.x
3.  把tools目录下的`ok_make_db.py`文件放到唐诗数据目录下，双击运行此脚本生成`tangshi.db.zip`文件。
4.  把生成的`tangshi.db.zip`放到`\app\src\main\assets\databases`目录下，此时需要手工创建databases目录。

### 感谢
使用了jackeyGao网友整理的全唐诗数据库（原始数据为繁体字）：  
https://github.com/jackeyGao/chinese-poetry

参考了OpenCC提供的繁体->简体转换表：  
https://github.com/BYVoid/OpenCC

使用的其它开源项目：[标签控件](https://github.com/whilu/AndroidTagView)，[assets数据库支持](https://github.com/jgilfelt/android-sqlite-asset-helper) ，[okhttp](https://github.com/square/okhttp)。
