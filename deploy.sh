#!/bin/bash
# Check the operating system
if [ -f /etc/centos-release ]; then
  # CentOS / Red Hat
  sudo yum install -y java-1.8.0-openjdk
  echo y | sudo yum install java-1.8.0-openjdk-devel

  echo 'export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk' >> ~/.bashrc

  echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.bashrc
  source ~/.bashrc

elif [ -f /etc/debian_version ]; then
  # Ubuntu / Debian
  sudo apt-get update
  sudo apt-get install -y openjdk-8-jdk
elif [ -f /etc/os-release ]; then
  # Other Linux distributions (fallback)
  source /etc/os-release
  if [[ "${ID}" == "arch" ]]; then
    # Arch Linux
    sudo pacman -S jdk8-openjdk
  elif [[ "${ID}" == "alpine" ]]; then
    # Alpine Linux
    sudo apk add openjdk8
  else
    echo "Unsupported Linux distribution: ${ID}"
    exit 1
  fi
elif [[ "$OSTYPE" == "darwin"* ]]; then
  # Mac
  brew tap adoptopenjdk/openjdk
  brew cask install adoptopenjdk8
else
  echo "Unsupported OS: $OSTYPE"
  exit 1
fi

# Verify Java installation
java -version
sudo chmod -R 777 .
# 指定目标目录为当前目录
target_directory="."

# 在目标目录中查找名字以"server"结尾的.jar文件
find "${target_directory}" -type f -name "server*.jar" -print0 | while IFS= read -r -d '' file; do
    # 输出包含目标文件的目录
    directory=$(dirname "${file}")
    echo "目录: ${directory}"
done
# 获取当前目录
current_directory=$(pwd)

# 打印当前目录
echo "当前目录: ${current_directory}"
JAR_FILE="/home/admin/application/boost.server/target/boost.server-1.0.0-SNAPSHOT.jar"

# 检查是否有同名的 Java 进程在运行
PID=$(jps -mlv | grep "$JAR_FILE" | awk '{print $1}')

if [ -n "$PID" ]; then
  echo "Found running process with PID: $PID"

  # 发送关闭信号给程序
  kill -9 $PID

  # 等待一段时间，确保程序关闭
  sleep 5

  # 再次检查是否还有进程在运行
  PID=$(jps -mlv | grep "$JAR_FILE" | awk '{print $1}')

  if [ -n "$PID" ]; then
    echo "Failed to gracefully stop the process with PID: $PID"
    exit 1
  else
    echo "Successfully stopped the process"
  fi
fi
sudo nohup java -jar /home/admin/application/boost.server/target/boost.server-1.0.0-SNAPSHOT.jar > /dev/null 2>&1 &
