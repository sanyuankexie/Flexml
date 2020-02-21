## intellij 插件
### 1 这是什么？
这是Flexml的intellij平台插件，适用于Android Studio和IDEA。

### 2 我要改你的代码

如果需要修改源码和自行编译，请用IDEA打开（否则编译不了不要怪我😂）。Jetbrains还没有开放在Android Studio上编写intellij平台插件的功能。
### 3 我想直接使用
插件您可以在Github的[release](https://github.com/sanyuankexie/Flexml/releases)界面找到。但暂时还没有发布到Jetbrains的官方插件仓库，所以暂时需要您从磁盘手动安装。

这里我以Windows下的操作为例子，在Android Studio上依次找到的File->Setting->Plugins。
<img src="https://i.loli.net/2020/02/21/xEMjaQe9nhsW7qT.png" style="zoom: 80%;" />
点击install from disk，找到插件的jar执行安装后重启Android Studio。
<img src="https://i.loli.net/2020/02/21/oiYmGT2PdZs5E1C.png" style="zoom: 80%;" />
等你的项目初始化完毕并且右键菜单出现这两个图标的时候，表示插件已经成功安装并正常运行。

现在Android Studio可以识别后缀.flexml的文件并为其提供一些自动补全功能。点击左上角的小箭头，可以将布局编译成json。
<img src="https://i.loli.net/2020/02/21/vB871NSb3wMPpqy.png" style="zoom:80%;" />
Flexml以**包**为单位管理布局文件和mock数据，一个**文件夹+文件夹下的package.json**作为一个包的基本结构，当你选中package.json文件的时候，点击右边的小箭头即可打开调试服务。
![](https://i.loli.net/2020/02/21/6PugQDTYzCdE9Wi.png)
现在您可以使用用playground app扫描电脑上的二维码（app左上角的扫码按钮），开始实时预览。在编写完成一段代码之后按`Ctrl+S`可以将布局实时同步到真机上。

<img src="https://i.loli.net/2020/02/21/ekBAgCSdrMWcOI8.jpg" style="zoom: 25%;" />

值得注意的是，如果您切换编辑的布局，您并不需要停止调试服务并重新开启，Android Studio会追踪您正在编辑的布局文件，并将其同步到您的手机上。

**好了，如果我用的是放洋屁的Apple，你这玩意Mac能用吗？**

插件是本身是跨平台的，已在我的放洋屁的macbook pro 2016上经过测试。

**注意**：playground app可以使用0.3.0版本，但是插件请使用0.3.1版本，特别是MacOs。

**PS**：JDK的`java.net`包在不同平台上的行为居然**大相径庭**，实在是让我倍感意外，我一直以为只有Android才会这样......





