#!/bin/bash
TOOL=$1
shift
TOOLTEST="${TOOL}Test"
echo $TOOL >> src/main/resources/tools.txt
sed -e "s/TemplateTool/$TOOL/g" src/main/java/nl/utwente/bigdata/TemplateTool.java > src/main/java/nl/utwente/bigdata/$TOOL.java
sed -e "s/TemplateToolTest/$TOOLTEST/g" -e "s/TemplateTool/$TOOL/g" src/test/java/nl/utwente/bigdata/TemplateToolTest.java > src/test/java/nl/utwente/bigdata/$TOOLTEST.java