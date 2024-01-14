@echo off
chcp 65001
cd D:\Minecraft_Client_modding\SideStream1.17.1-versionNametag
SET INPUTSTR=
git status
git add *
SET /P INPUTSTR="コミット時のコメントを入力して下さい:"
git commit -m "%INPUTSTR%"
git status
git push origin master
pause