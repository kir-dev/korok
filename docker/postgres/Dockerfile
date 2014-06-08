# PostgreSQL container for VIR

# BUILD-USING:        $ docker build -t pek-postgres-img .
# RUN-USING:          $ docker run -itd -p 5432:5432 --volumes-from pek-data -e POSTGRESQL_PASS=dev --name pek-postgres pek-postgres-img

FROM ubuntu:14.04

MAINTAINER Balazs Varga "https://blog.vbalazs.me"

ENV PG_VERSION 9.3
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
RUN sed 's#archive.ubuntu#hu.archive.ubuntu#g' -i /etc/apt/sources.list

RUN apt-get -qq -y update && \
    apt-get install -y -q --no-install-recommends postgresql \
                    postgresql-client postgresql-contrib

RUN echo "host all all 0.0.0.0/0 md5" >> /etc/postgresql/$PG_VERSION/main/pg_hba.conf
RUN echo "listen_addresses='*'" >> /etc/postgresql/$PG_VERSION/main/postgresql.conf

ADD run.sh /usr/local/bin/run.sh
RUN chmod +x /usr/local/bin/run.sh

EXPOSE 5432

CMD ["/usr/local/bin/run.sh"]

