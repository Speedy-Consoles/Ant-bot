JAVAC=javac
JAVAC_ARGS=

SOURCES=SpeedBot.java
CLASSES=$(SOURCES:%.java=%.class)

.PHONY: all clean

all: $(CLASSES)

%.class: %.java
	$(JAVAC) $(JAVAC_ARGS) $<

clean:
	-rm -Rf *.class
