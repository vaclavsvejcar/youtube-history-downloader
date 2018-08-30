# YouTube History Downloader

Have you ever wanted to see your full YouTube watching history, but found out that YouTube doesn't allow you to do that? Scrolling down the history to get records more than few days back would be really a awful experience. Fortunately, here comes the _Youtube History Downloader_, small tool written in [Scala](https://www.scala-lang.org), that allows you to fetch your entire history within few minutes!

> Please note this tool is still in development, so it might not work 100% and some functionality and documentation might be missing.

## Main Features
- multiplatform (running on [Java](https://java.com/en/), supports _macOS_, _Windows_, _GNU/Linux_)
- downloads history into _CSV_ file
- generates fancy _HTML_ report from the _CSV_ file

## Planned Features
- proper GUI to make it more friendly for people without command-line knowledge
- refactor the main logic as a proper library so you can use it in your code
- improve the HTML report (more options, better performance)

## Get the YouTube cookies
In order that _Youtube History Downloader_ can fetch your _YouTube_ history, you must copy the _YouTube_-related cookies from your web browser into the `cookies.txt` file placed inside the project main directory. Here are steps how to do it:

1. Open [YouTube](https://youtube.com) in your web browser.
1. Find one of the request in _Developer tools_ and copy the _Cookie_ HTTP request header content.
   <p align="center"><img src ="https://raw.githubusercontent.com/vaclavsvejcar/ytb-history-downloader/master/readme-data/cookies.jpg" width="400" /></p>
1. Copy that string into the `cookies.txt` file. The content should look like this:
   ```
   VISITOR_INFO1_LIVE=someTokenHere; PREF=f1=someTokenHere; CONSENT=YES+CZ.cs+20151207-13-0; YSC=someTokenHere; GPS=1;     SID=someTokenHere; HSID=someTokenHere; SSID=someTokenHere; APISID=T-someTokenHere/someTokenHere; SAPISID=someTokenHere; LOGIN_INFO=someTokenHere==
   ```
   
## Run from SBT
1. Clone this repository: `git clone https://github.com/vaclavsvejcar/ytb-history-downloader.git`
1. Enter SBT console: `$ sbt`
1. Fetch the history using the `> run fetch` command. This will save the history data into the `history.csv` file. Now you can open this file in your favorite CSV editor, or generate the HTML report.
1. You can generate the HTML report using `> run report` command. This will generate HTML file `report.html`.
   > Please note that for large history files, opening the report in web browser may take some time.

<p align="center"><img src ="https://raw.githubusercontent.com/vaclavsvejcar/ytb-history-downloader/master/readme-data/screenshot.jpg" /></p>

## Run from production binaries
> coming soon!

## Next steps
Are you missing some cool feature or have you found a bug? Feel free to open new issue, or create a merge request :-)
