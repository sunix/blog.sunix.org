FROM sunix/jekyll4che

ENV CODENVY_APP_BIND_DIR /home/user/app
VOLUME ["/home/user/app"]

USER root
WORKDIR /home/user/app

CMD echo 'url: "http://'$CODENVY_HOSTNAME':'$CODENVY_PORT_4000'"' >> _config.yml && \
    echo 'baseurl: ""' >> _config.yml && \
    jekyll serve