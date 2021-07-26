

echo "update..."
#sudo apt-get update

echo "install vim..."
sudo apt-get install -y vim

echo "alias vi=vim"
alias vi=vim

echo "install fcitx rime"
sudo add-apt-repository ppa:fcitx-team/stable && sudo apt-get update
sudo apt-get install -y fcitx-rime
echo "config fcitx..."
im-config

echo "install pip"
sudo apt-get install -y python-pip

echo "install git..."
sudo apt-get install -y git

echo "install ShadowSocks..."
sudo apt-get install -y shadowsocks
#pip install git+https://github.com/shadowsocks/shadowsocks.git@master

echo "start shadowsocks..."
sudo sslocal -c /etc/shadowsocks.json -d start

echo "install mp3,mp4..."
sudo apt install ubuntu-restricted-extras

echo "install maven..."
sudo apt install -y maven

echo "install java, mkdir /usr/local/java directory..."
sudo mkdir /usr/local/java/


echo "config vim..."
echo "set nu" >> ~/.vimrc
echo "set autointend" >> ~/.vimrc

echo "add shadowsocks to startup"
sudo touch /etc/rc.local
sudo chmod +x /etc/rc.local
sudo echo "#!/bin/bash" >> /etc/rc.local
sudo echo "sslocal -c /etc/shadowsocks.json -d start" >> /etc/rc.local
sudo echo "exit 0"  >> /etc/rc.local
