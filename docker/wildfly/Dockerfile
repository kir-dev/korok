# Wildfly container for VIR

# BUILD-USING:        $ docker build -t pek-wildfly-img .
# RUN-USING:          $ docker run -itdP --volumes-from pek-data --link pek-postgres:db --name pek-wildfly pek-wildfly-img

FROM ubuntu:14.04

MAINTAINER Balazs Varga "https://blog.vbalazs.me"

# Resource files stored in the pek-data volume container
ENV PEK_APPDATA /data/appdata

ENV INSTALL_DIR /var/appserver
ENV WILDFLY_DIR /var/appserver/wildfly
ENV WILDFLY_VERSION 8.1.0.Final
ENV PG_JDBC_VERSION 9.3-1101
ENV WILDFLY_USER wildfly

ENV DEBIAN_FRONTEND noninteractive

# Try to avoid policy.d warnings
RUN dpkg-divert --local --rename --add /sbin/initctl &&\
    sh -c "test -f /sbin/initctl || ln -s /bin/true /sbin/initctl"

# Configure timezone and locale
RUN echo "Europe/Budapest" > /etc/timezone; dpkg-reconfigure -f noninteractive tzdata
ENV LANGUAGE en_US.UTF-8
ENV LANG en_US.UTF-8
ENV LC_ALL en_US.UTF-8

RUN locale-gen en_US.UTF-8 && \
    dpkg-reconfigure locales && \
    update-locale LANG=en_US.UTF-8 && \
    locale-gen en_US.UTF-8

# use Hungarian apt mirror
RUN sed 's#archive.ubuntu#hu.archive.ubuntu#g' -i /etc/apt/sources.list && \
# add oracle jdk ppa
    (echo "deb http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main" >> /etc/apt/sources.list) && \
    apt-key adv --keyserver keyserver.ubuntu.com --recv-keys EEA14886

RUN apt-get -q -y update && \
    apt-get -q -y upgrade && \
# automatically accept oracle license
    echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections && \
# install requirements 
    apt-get install -y -q oracle-java8-installer oracle-java8-set-default \
                   curl git unzip vim openssh-server && \
    apt-get clean

# Setup SSH
RUN mkdir /var/run/sshd && \
    sed -ri 's/UsePAM yes/#UsePAM yes/g' /etc/ssh/sshd_config && \
    sed -ri 's/#UsePAM no/UsePAM no/g' /etc/ssh/sshd_config && \
    sed -ri 's/PermitRootLogin without-password/PermitRootLogin yes/g' /etc/ssh/sshd_config && \
    echo 'root:dev123' |chpasswd 

ADD wildfly-install.sh /opt/wildfly-install.sh
RUN chmod +x /opt/wildfly-install.sh
RUN /opt/wildfly-install.sh

EXPOSE 8080 9990 22

ADD run.sh /usr/local/bin/run.sh
RUN chmod +x /usr/local/bin/run.sh

CMD env | grep _ >> /etc/environment && /usr/local/bin/run.sh

