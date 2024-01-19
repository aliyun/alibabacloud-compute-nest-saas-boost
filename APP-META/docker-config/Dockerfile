FROM adoptopenjdk/openjdk8:centos

WORKDIR /home/admin/compute-nest-saas-boost/

RUN mkdir -p /home/admin/compute-nest-saas-boost/logs

COPY compute-nest-saas-boost.tgz /home/admin/compute-nest-saas-boost/

RUN tar -zxvf compute-nest-saas-boost.tgz \
    && mv target/*.jar ./

EXPOSE 9999

ENTRYPOINT [ "java" ]

CMD [ "-jar", "boost.server-1.0.0-SNAPSHOT.jar" ]
