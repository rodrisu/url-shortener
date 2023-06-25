#!/bin/bash

table_name=$1
endpoint_url=$2
region=$3

aws dynamodb create-table \
    --table-name "$table_name" \
    --attribute-definitions AttributeName=key,AttributeType=S \
    --key-schema AttributeName=key,KeyType=HASH \
    --billing-mode PAY_PER_REQUEST \
    --endpoint-url "$endpoint_url" \
    --region "$region"
