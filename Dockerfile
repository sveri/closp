FROM java:8

RUN wget https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein
RUN mv lein /usr/local/bin
RUN chmod +x /usr/local/bin/lein

ENV LEIN_ROOT true

RUN lein new closp closp_showcase -n foo.bar

WORKDIR /closp_showcase

RUN lein rel-jar

EXPOSE 3000

CMD java -jar /closp_showcase/target/closp_showcase.jar
