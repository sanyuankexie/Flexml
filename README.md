<img src="https://i.loli.net/2020/02/20/MnxSy9cOAaJQrpX.jpg" width="100%" align="middle"/>

# Flexml  ![](https://img.shields.io/badge/language-kotlin%20%7C%20java-orange) ![](https://jitpack.io/v/LukeXeon/flexbox.svg) ![](https://img.shields.io/badge/platform-android-brightgreen) ![](https://img.shields.io/badge/license-Apache--2.0-blue) ![](https://img.shields.io/badge/email-imlkluo%40qq.com-green) ![](https://img.shields.io/badge/doc-%E6%8E%98%E9%87%91-blue) ![](https://img.shields.io/badge/API-19%2B-green)

### 0 注意
**Gbox**已经被重命名为**Flexml**！目前SDK和playground app最新版本为**0.3.0**，插件最新版本为**0.3.1**，**MacOS**用户请一定使用**0.3.1**版本的插件。

### 1 适用的业务范围

在线上，对于某些适用于要求强展示、轻交互、高可配场景，我们有三种方案：
* RN
* WebView
* RecyclerView

这三种方案各有各的问题。

使用RN时要占据整个Activity，而且Native和Js的通信损耗不可避。在另一边，WebView的情况则更加糟糕，需要lock主线程来加载webkit。所以这两种方案一般在这在二级、三级页面使用，如果在首页使用其实并不是非常理想。

那，**RecyclerView**呢？RecyclerView需要使用viewType来区分Holder，并且在每种Holder首次创建时都会执行预分配，最要命的是它还要测量布局，一下子测量那么多布局还是必须在主线程执行的操作，16ms的帧间隔只会让你铁掉帧。而且，如果我们基于viewType来实施动态化，那么Holder要怎么写？要写多少种viewType，样式有变怎么办？

什么！你说动态下发xml？Google：“想都别想，我用native API直接给你把路堵死，顺便还提升了我的性能”。

对于首页的feed流卡片、广告等，这些页面的逻辑本身就不强，而且往往也只是需要局部动态化，所以综合来看，RN和WebView硬着头皮上虽然还是能用的，但对性能的妥协就太大了，所以需要一个小型化的动态方案，并且在性能上达到极致。

### 2 简介
Flexml就是一个小型化的动态方案，专注于**单View**的动态化，它基于facebook的litho和google推荐的glide，可以这么说，它跟RN，还有点像...

但不同于传统的View的是，Flexml没有复杂的View层级，所以即使你在代码里套了**1000**层，它显示的可能也只有一个View。

Flexml使用Drawable直接将业务样式（文本、图片、圆角、颜色、边框等等）绘制在单个View之上，这将大大降低复杂布局在内存中的View数量（由**N→1**）。

你可以打开开发者模式查看View边界，这时往往只会看到一个View，除非是为了处理点击事件之类与View强相关的问题时才会自动的多生成一个VIew。

<img src="https://i.loli.net/2020/02/20/uQYVKFM38ROS6Bj.jpg" style="zoom: 33%;" />

Flexml不使用任何非硬件加速的API，你看到的所有图像都是由**硬件加速**的API支持的，特别是在圆角处理上，不开玩笑几乎无敌。

* 不是**clipPath**，我们有抗锯齿
* 不是**clipOutline**，我们支持**4个角**设置**不同**的弧度并兼容api 19

但其原理也很简单，那就是使用**Shader**并配合**Paint**，在无圆角时直接**drawRect**，有相同圆角时使用**drawRoundRect**，在有不同的圆角时才使用**drawPath**，这种做法也是Android api 28中**GradientDrawable**实现圆角的做法。当然，我也将它们移植到了ColorDrawable和BitmapDrawable。

Flexml支持布局预加载，并且会在**后台线程**提前把布局测量好，不卡主线程。等需要显示的时候布局早已完成测量，会直接跳过**measure**环节直接就可以绘制，弯道超车完美解决**16ms**的vsync限制。

Flexml拥有良好的资源回收能力，它使用流行的**Glide**作为图片加载框架并结合**litho**的可见性感知api，能使你滑出屏幕的每一张图片都会被**Glide**有效回收。

Flexml提供一个**playground app**，**playground app**是一个集**样例代码展示**以及**提供实时预览开发**功能的app。

下面的截图，也是由Flexml绘制是直接从**playground app**中截取的，你可以在本仓库的[release](https://github.com/sanyuankexie/Flexml/releases)界面找到**apk**下载安装尝试，或者直接索取该布局的源码[introduction/template.flexml](https://github.com/sanyuankexie/Flexml/blob/master/playground/src/main/assets/layout/introduction/template.flexml)。
<img src="https://i.loli.net/2020/02/20/RZyCksOHtN37FDo.jpg" style="zoom:33%;" />

其实就连你在最开头看到的logo都是Flexml自绘制的，对应的源码在这[logo/template.flexml](https://github.com/sanyuankexie/Flexml/blob/master/playground/src/main/assets/layout/logo/template.flexml)。

Flexml支持在真机上实时预览，为实现该功能，开发了Intellij（Android Studio）插件，配合**playground app**可以实时在真机上调试布局。

### 3 Wiki
集成、试用以及其他相关资料，请查看Github上的[wiki页面](https://github.com/sanyuankexie/Flexml/wiki)。
### 4 展望
未来，Flexml的目标是向iOS进军，在iOS设备上完成一套等价的SDK，得益于facebook和google强大的开源生态，所以这是可行的。

* facebook [litho](https://github.com/facebook/litho)在iOS上的等价物是facebook [AsyncDisplayKit（现在又叫Texture）](https://github.com/texturegroup/texture/)。
* el完全满足google [j2objc](https://github.com/google/j2objc)项目的代码转制要求。

剩下的就是时间问题。

### 5 关于开源
Flexm使用kotlin开发，在Apache 2.0开源协议下发布，是一个完全基于开源软件实现的开源软件。由[@LukeXeon](https://github.com/LukeXeon)维护。

Flexml是一个比较新的litho社区开源项目，有关其他其他facebook litho的社区开源项目，请在facebook的litho [Community Showcase](https://fblitho.com/docs/community-showcase)查找。

