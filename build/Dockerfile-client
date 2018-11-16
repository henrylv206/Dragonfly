FROM docker.io/centos:latest

COPY df-client /root/df-client
COPY run.sh /root/df-client/run

ENV PATH=/root/df-client:$PATH

EXPOSE 80

WORKDIR /root/

CMD ["run"]
