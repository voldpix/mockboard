#!/bin/bash

# chmod +x <script>.sh

URL="http://localhost:8000/api/boards"
TOTAL_RECORDS=10000
CONCURRENCY=10

echo "Creating $TOTAL_RECORDS records to $URL with concurrency of $CONCURRENCY..."

seq 1 $TOTAL_RECORDS | xargs -I {} -P $CONCURRENCY curl -s -X POST "$URL" \
    -H "Content-Type: application/json" \
    -o /dev/null

echo -e "\nFinished creating $TOTAL_RECORDS records."