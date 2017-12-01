#!/bin/sh

if [ $# -ne 1 ]; then
    echo "usage: host:port";
    exit 1;
fi

mx=640;my=480;head -c "$((3*mx*my))" /dev/urandom | convert -depth 8 -size "${mx}x${my}" RGB:- random.jpg
curl -O -J -v -F "myfile=@random.jpg" -L $1
