2025-6-3

#### 集成组使用说明

1. 本模块编译生成的jar包路径为：out\target\common\obj\JAVA_LIBRARIES\android.car_intermediates\classes-header.jar
2. 集成组同事可在仓库根目录创建临时目录tmp，并将classes-header.jar拷贝到tmp目录中，然后执行tools目录下的publish.sh脚本；
3. 脚本执行完成后，tmp目录下将生成sdk发布包和需要上传maven的jar包，请集成组同事取走文件并删除tmp目录。

#### 开发使用说明

1. 开发在本模块下面创建release目录，包含以下内容：
    --tools
    ----javadocGen.sh
    ----publish.sh
    --README.md
    --ReleaseNotes.md
2. 开发修改javadocGen.sh中java文件路径和文件名
3. 开发将本模块的路径和包名发给集成，集成脚本中添加该路径和包名
4. 开发每次修改代码后都需要修改ReleaseNotes.md

