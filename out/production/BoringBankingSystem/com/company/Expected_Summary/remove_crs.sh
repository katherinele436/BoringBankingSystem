#!/bin/bash
for i in *.*
do
    sed -i 's/\r//g' $i
done
