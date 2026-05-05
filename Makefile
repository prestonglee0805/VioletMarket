SRC = $(shell find src -name "*.java")

compile:
	mkdir -p out
	javac -d out $(SRC)

run: compile
	java -cp out cli.Main

clean:
	rm -rf out
