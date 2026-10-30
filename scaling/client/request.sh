#!/bin/sh

if [ $# -ne 1 ]; then
    echo "usage: host:port";
    exit 1;
fi

# A fresh image for each call: concurrent clients must not share the same file.
img=$(mktemp)
mx=640;my=480;head -c "$((3*mx*my))" /dev/urandom | convert -depth 8 -size "${mx}x${my}" RGB:- jpg:${img}
curl -s -S -o /dev/null -F "myfile=@${img};filename=random.jpg" $1
rm -f ${img}
