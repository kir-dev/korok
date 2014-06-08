# Data Volume Container

# BUILD-USING:        $ docker build -t pek-data-img .
# RUN-USING:          $ docker run --name pek-data pek-data-img

FROM busybox:ubuntu-14.04

MAINTAINER Balazs Varga "https://blog.vbalazs.me"

VOLUME ["/data", "/var/lib/postgresql"]

# copy app settings
ADD appdata /data/appdata/
RUN chmod -R a+rwx /data/

# dummy, fast-exit
CMD ["/bin/true"]

