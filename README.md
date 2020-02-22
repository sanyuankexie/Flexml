<img src="https://i.loli.net/2020/02/20/MnxSy9cOAaJQrpX.jpg" width="100%" align="middle"/>

# Flexml  ![](https://img.shields.io/badge/language-kotlin%20%7C%20java-orange) ![](https://jitpack.io/v/LukeXeon/flexbox.svg) ![](https://img.shields.io/badge/platform-android-brightgreen) ![](https://img.shields.io/badge/license-Apache--2.0-blue) ![](https://img.shields.io/badge/email-imlkluo%40qq.com-green) ![](https://img.shields.io/badge/doc-%E6%8E%98%E9%87%91-blue) ![](https://img.shields.io/badge/API-19%2B-green)

### 0 注意
**Gbox**已经被重命名为**Flexml**！目前SDK和playground app最新版本为**0.3.0**，插件最新版本为**0.3.1**，**MacOS**用户请一定使用**0.3.1**版本的插件。

### 1 适用的业务范围

在线上，对于某些适用于要求强展示、轻交互、高可配场景，RN和WebView显得不够灵活，性能表现也不够好。

使用RN时要占据整个Activity，而且Native和Js的通信损耗不可避，WebView的情况则更加糟糕，还要lock主线程来加载webkit。这在二级、三级页面还好，在首页使用上述两种方案效率显然不行。

但是对于首页feed流卡片、一级页面的活动区块来说，这些页面的逻辑本身就不强，而且往往也只是需要局部动态化，所以综合来看RN和WebView都不是最优选，所以我们需要**第三条路**。

### 2 简介
Flexml就是为了解决以上的问题而诞生的，它基于facebook的litho和google推荐的glide。

Flexml不使用传统View的框架，而使用Drawable直接将图像绘制在单个View之上，这将大大降低复杂布局在内存中的View数量（由**N→1**）。所以你在开发者模式中打开View边界时，往往只会看到一个View，除非是为了处理点击事件之类与View强相关的问题时才会自动的多生成一个VIew。

<img src="https://i.loli.net/2020/02/20/uQYVKFM38ROS6Bj.jpg" style="zoom: 33%;" />

Flexml不使用任何非硬件加速的API，你看到的所有图像都是由**硬件加速**的API支持的，特别是在圆角处理上，不开玩笑几乎无敌，不是**clipPath**，我们有抗锯齿，不是**clipOutline**，我们支持**4个角**设置**不同**的弧度并兼容api 19。其原理也很简单，那就是使用**BitmapShader**并配合**Paint**，在无圆角时直接**drawRect**，有相同圆角时使用**drawRoundRect**，在有不同的圆角时才使用**drawPath**，这种做法也是Android api 28中**GradientDrawable**实现圆角的做法。

Flexml支持布局预加载，会在**异步线程**提前把布局测量好，等需要显示的时候布局早已完成测量，会直接跳过**measure**环节直接就可以绘制，弯道超车完美解决**16ms**的vsync限制。

Flexml拥有良好的资源回收能力，它使用最流行的Glide作为图片加载框架并结合litho的可见性感知api，能使你滑出屏幕的每一张图片都会被有效回收。

Flexml提供一个**playground app**，**playground app**是一个集**样例代码展示**以及**提供实时预览开发**功能的app。

下面的截图，也是由Flexml绘制是直接从**playground app**中截取的，你可以在本仓库的[release](https://github.com/sanyuankexie/Flexml/releases)界面找到**apk**下载安装尝试，或者直接索取该布局的源码[introduction/template.flexml](https://github.com/sanyuankexie/Flexml/blob/master/playground/src/main/assets/layout/introduction/template.flexml)。
<img src="https://i.loli.net/2020/02/20/RZyCksOHtN37FDo.jpg" style="zoom:33%;" />

其实就连你在最开头看到的logo都是Flexml自绘制的，对应的源码在这[logo/template.flexml](https://github.com/sanyuankexie/Flexml/blob/master/playground/src/main/assets/layout/logo/template.flexml)。

### 4 提供Intellij插件
为了实现实时预览功能，需要扩展IDE能力的边界，为此开发了Intellij（Android Studio）插件并向大家提供，配合**playground app**可以实时在真机上调试布局。

进一步了解更多插件相关信息，请转到插件模块的[README.md](https://github.com/sanyuankexie/Flexml/blob/master/intellij-plugin/README.md)。

插件的release版本你也可以在本仓库的[release](https://github.com/sanyuankexie/Flexml/releases)界面找到。
### 4 使用jitpack集成SDK

添加到你的根build.gradle。

```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

添加到你所使用的模块。

```
	dependencies {
	        implementation 'com.github.sanyuankexie.Flexml:litho:0.3.0'
	}
```

接下来你需要从json中解析得到`com.guet.flexbox.TemplateNode`，这玩意是未绑定数据的模板文件，由模板编译器编译得到（[模板编译器与Intellij插件一起发布，请参考插件页面](https://github.com/sanyuankexie/Flexml/blob/master/intellij-plugin/README.md)），使用Gson和FastJson都可以顺利完成解析。

```kotlin
package com.guet.flexbox

class TemplateNode(
        val type: String,
        val attrs: Map<String, String>?,
        val children: List<TemplateNode>?
)
```
然后创建`com.guet.flexbox.litho.TemplatePage`，注意，此过程必须不在主线程执行，因为build函数会执行模板解析和数据绑定并完成布局测量，会占用大量的CPU时间。
```kotlin
TemplatePage.create(c)
        .template(template)//也就是TemplateNode的实例。
        .data(myData)//JSONObject，Map，标准JavaBean（有getter，setter的）都可以。
        .build()
```
在TemplatePage实例化完成之后，将它赋值给`com.guet.flexbox.litho.HostingView`的`templatePage`，即可完成View的展示。
```kotlin
hostingView.templatePage = yourTemplatePage
```
注意，不要将TemplatePage同时给两个HostingView使用，当你不用某个TemplatePage时，记得将它`release`。
```kotlin
yourTemplatePage.release()
```
如果你要在RecyclerView中使用，你应该这么写。
```kotlin
            override fun onBindViewHolder(
                    holder: HostingViewHolder,
                    position: Int
            ) {
                val tree = trees[pos]
                holder.hostingView.componentTree = tree
            }

            override fun onViewRecycled(holder: HostingViewHolder) {
                //下面这两行很关键，涉及到资源的正确回收，一定不能不写
                holder.hostingView.unmountAllItems()
                holder.hostingView.componentTree = null
            }
```
如果你需要接入新的Native View，可以参考litho的官方文档[Mount Specs](https://fblitho.com/docs/mount-specs)，或者参考我为了ViewPager2写的[BannerSpec.kt](https://github.com/sanyuankexie/Flexml/blob/master/litho/src/main/java/com/guet/flexbox/litho/widget/BannerSpec.kt)进行接入。

更多资料请看下面的wiki。

### 6 Wiki
[Github上的wiki页面](https://github.com/sanyuankexie/Flexml/wiki)

### 7 展望
未来，Flexml的目标是向iOS进军，在iOS设备上完成一套等价的SDK，得益于facebook和google强大的开源生态，所以这是可行的。

* facebook [litho](https://github.com/facebook/litho)在iOS上的等价物是facebook [AsyncDisplayKit（现在又叫Texture）](https://github.com/texturegroup/texture/)。
* tomcat el完全满足google [j2objc](https://github.com/google/j2objc)项目的代码转制要求。

剩下的就是时间问题。

### 8 关于开源
Flexm使用kotlin开发，在Apache 2.0开源协议下发布，是一个完全基于开源软件实现的开源软件。由[@LukeXeon](https://github.com/LukeXeon)维护。

Flexml时一个比较新的litho社区开源项目，有关其他其他facebook litho的社区开源项目，请在facebook的litho [Community Showcase](https://fblitho.com/docs/community-showcase)查找。

