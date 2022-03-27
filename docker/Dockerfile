FROM  centos:7

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

RUN echo 'Asia/Shanghai' >/etc/timezone

RUN yum -y install kde-l10n-Chinese && yum -y reinstall glibc-common 


RUN localedef -c -f UTF-8 -i zh_CN zh_CN.utf8 

ENV LC_ALL zh_CN.utf8

RUN  yum install java-1.8.0-openjdk* -y

RUN  mkdir  /data/

RUN  mkdir  /data/projects

WORKDIR /data/projects/

ADD  flink-streaming-platform-web.tar.gz  /data/projects/

ADD  flink-1.13.2.tar      /data/projects/

COPY  app-start.sh  /data/projects/

COPY application-docker.properties  /data/projects/flink-streaming-platform-web/conf/

COPY info.log /data/projects/flink-streaming-platform-web/logs/


RUN   cd  /data/projects/

RUN  chown  -R root:root flink-streaming-platform-web

RUN  chown  -R root:root flink-1.13.2

ENTRYPOINT bash  app-start.sh

EXPOSE  9084 5007   8081
 


