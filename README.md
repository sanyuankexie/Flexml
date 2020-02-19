<img src="https://i.loli.net/2020/02/20/MnxSy9cOAaJQrpX.jpg" width="100%" align=”middle“/>

# Flexml  ![](https://img.shields.io/badge/language-kotlin%20%7C%20java-orange) ![](https://jitpack.io/v/LukeXeon/flexbox.svg) ![](https://img.shields.io/badge/platform-android-brightgreen) ![](https://img.shields.io/badge/license-Apache--2.0-blue) ![](https://img.shields.io/badge/email-imlkluo%40qq.com-green) ![](https://img.shields.io/badge/doc-%E6%8E%98%E9%87%91-blue) ![](https://img.shields.io/badge/API-19%2B-green)

### 0 注意
**Gbox**已经被重命名为**Flexml**！目前最新版本为**0.3.0**。

### 1 适用的业务范围

在线上，对于某些适用于要求强展示、轻交互、高可配场景，RN和WebView显得不够灵活，性能表现也不够好。

使用RN时要占据整个Activity，而且Native和Js的通信损耗不可避，WebView的情况则更加糟糕，还要lock主线程来加载webkit。这在二级、三级页面还好，在首页使用上述两种方案效率显然不行。

但是对于首页feed流卡片、一级页面的活动区块来说，这些页面的逻辑本身就不强，而且往往也只是需要局部动态化，所以综合来看RN和WebView都不是最优选，所以我们需要**第三条路**。

### 2简介
Flexml就是为了解决以上的问题而诞生的，它基于facebook的litho和google推荐的glide。

不使用传统View的框架使用Drawable直接将图像绘制在单个View之上，所以你在开发者模式中打开View层级只会看到一个View。
![](https://i.loli.net/2020/02/20/uQYVKFM38ROS6Bj.jpg)

Flexml不使用非硬件加速的API，你看到的所有图像都是由**硬件加速**的API支持的，特别是在圆角处理上，不开玩笑几乎无敌，不是**clipPath**，我们有抗锯齿，不是**clipOutline**，我们支持**4个角**设置**不同**的弧度，最终是使用**BitmapShader**实现了该特性。

同时Flexml会在**异步线程**提前把布局测量好，等需要显示的时候布局早已完成测量，跳过**measure**环节直接就可以绘制，弯道超车完美解决**16ms**限制导致的卡顿问题。

Flexml使用最流行的Glide作为图片加载框架，所以拥有良好的资源回收能力，当你将View滑出屏幕的时候图片就会被自动回收，图多顶多内存高点，但是不会OOM。

下面的截图，也是由Flexml绘制是直接从**playground app**中截取的，你可以在[release](https://github.com/sanyuankexie/Flexml/releases)界面找到**apk**下载安装尝试，或者直接索取该布局的源码[introduction/template.flexml](https://github.com/sanyuankexie/Flexml/blob/master/playground/src/main/assets/layout/introduction/template.flexml)。
![](https://i.loli.net/2020/02/20/RZyCksOHtN37FDo.jpg)

连你上面看到的logo都是自己画的，对应的源码在这[logo/template.flexml](https://github.com/sanyuankexie/Flexml/blob/master/playground/src/main/assets/layout/logo/template.flexml)。

### 4 提供Intellij插件
同时提供Intellij（Android Studio）插件，配合playground app可以实时在真机上调试布局，插件您可以在[release](https://github.com/sanyuankexie/Flexml/releases)界面找到。
### 5 开源
Gbox使用kotlin开发，在Apache 2.0开源协议下发布，是一个完全基于开源软件实现的开源软件。由[@LukeXeon](https://github.com/LukeXeon)维护（我不会跑路）。

