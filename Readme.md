# CTIT MapReduce Framework

Template project for mapreduce jobs on the CTIT Cluster.

## Cloning this project

To start with a map-reduce job, go to the cluster's head node and clone
this git repository.

```
ssh ctithead1.ewi.utwente.nl
git clone https://github.com/robinaly/ctit-mapred.git
cd ctit-mapred
```

## Compiling the project

To compile the contained java source code, use the following command (you
need to have maven installed):
```
	mvn package
```

## Running the word count example tool

Included in this project there is a word count example. 

Example: to run usage:

```
	. setenv
	runTool WordCount -input <DIR> -output <DIR>
```

The command ```.setenv``` defines a couple of directories and the function ```runTool```. Using the function ```runTool``` you can execute a tool
on the cluster.

## Creating a new tool

To create a new tool, say XYZ, execute:

```
./creatTool.sh XYZ
```

