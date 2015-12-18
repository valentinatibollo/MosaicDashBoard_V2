Before deploying MosaicDashboard remember to configure properly log4j.properties file (in src).
Set the location of the log file, for example:
log4j.appender.file.File=/opt/apache-tomcat-6.0.35/logs/mosaicDashBoard.log
(in my case the Dashboard is deployed using Tomcat 6.0.35, you will have to find the proper location in your Ubuntu VM)

Client-side logs can be downloaded using the button at top right of the window (near Logout button)
