ZoroProgressBar
======
**自定义View练习**

更新历史
-----
    2017/4/3:RoundProgressBar(仿360安全卫士圆形进度条),HorizontalProgressBar（水平进度条）,RoundProgressBar（圆形进度条）

使用依赖
----
在project下的build.gradle添加

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
    
在module下的build.gradle添加

 	dependencies {
		compile 'com.github.Helldefender:ZoroProgressBar:v0.1'
	}

自定义属性
-----
    <declare-styleable name="XLDownloadProgressBar">
        <attr name="xlDownload_frontColor" format="color" />
        <attr name="xlDownload_behindColor" format="color" />
        <attr name="xlDownload_backgroundColor" format="color" />
        <attr name="xlDownload_waveLength" format="dimension" />
        <attr name="xlDownload_waveHeight" format="dimension" />
        <attr name="xlDownload_waveInitialHeight" format="dimension" />
        <attr name="xlDownload_radius" format="dimension" />
        <attr name="xlDownload_borderWidth" format="dimension" />
        <attr name="xlDownload_textSize" format="dimension" />
        <attr name="xlDownload_textColor" format="color" />
        <attr name="xlDownload_progress" format="dimension" />
        <attr name="xlDownload_maxProgress" format="dimension" />
    </declare-styleable>

    <declare-styleable name="HorizontalProgressBar">
        <attr name="horizontal_backgroundColor" format="color" />
        <attr name="horizontal_backgroundRadius" format="dimension" />
        <attr name="horizontal_borderWidth" format="dimension" />
        <attr name="horizontal_textColor" format="color" />
        <attr name="horizontal_textSize" format="dimension" />
        <attr name="horizontal_textOffset" format="dimension" />
        <attr name="horizontal_progress_unReachHeight" format="dimension" />
        <attr name="horizontal_progress_unReachColor" format="color" />
        <attr name="horizontal_progress_reachHeight" format="dimension" />
        <attr name="horizontal_progress_reachColor" format="color" />
        <attr name="horizontal_finishColor" format="color" />
        <attr name="horizontal_progress" format="dimension" />
        <attr name="horizontal_maxProgress" format="dimension" />
    </declare-styleable>

    <declare-styleable name="RoundProgressBar">
        <attr name="round_borderWidth" format="dimension" />
        <attr name="round_textColor" format="color" />
        <attr name="round_textSize" format="dimension" />
        <attr name="round_progress_unReachHeight" format="dimension" />
        <attr name="round_progress_unReachColor" format="color" />
        <attr name="round_progress_reachHeight" format="dimension" />
        <attr name="round_progress_reachColor" format="color" />
        <attr name="round_finishColor" format="color" />
        <attr name="round_progress" format="dimension" />
        <attr name="round_maxProgress" format="dimension" />
        <attr name="round_radius" format="dimension" />
    </declare-styleable>

效果图
-----
![](https://github.com/Helldefender/ZoroProgressBar/raw/master/screenshot/start.png)
![](https://github.com/Helldefender/ZoroProgressBar/raw/master/screenshot/progress1.png)
![](https://github.com/Helldefender/ZoroProgressBar/raw/master/screenshot/progress2.png)
![](https://github.com/Helldefender/ZoroProgressBar/raw/master/screenshot/finish.png)

