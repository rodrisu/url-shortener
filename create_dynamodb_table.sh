#!/bin/bash

# Find the path to the application.properties file
propertiesFile=$(find . -name "application.properties" -path "./src/main/resources/*")

if [[ -z "$propertiesFile" ]]; then
  echo "Unable to locate the application.properties file."
  exit 1
fi

echo "Creating database in DynamoDB Local..."
# Read the values from the application.properties file
table_name=$(grep -E "^table.name=" "$propertiesFile" | cut -d'=' -f2)
endpoint_url=$(grep -E "^amazon\.dynamodb\.endpoint=" "$propertiesFile" | cut -d'=' -f2)
region=$(grep -E "^amazon\.dynamodb\.region=" "$propertiesFile" | cut -d'=' -f2)

# Run the AWS CLI command
aws dynamodb create-table \
    --table-name "$table_name" \
    --attribute-definitions AttributeName=key,AttributeType=S \
    --key-schema AttributeName=key,KeyType=HASH \
    --billing-mode PAY_PER_REQUEST \
    --endpoint-url "$endpoint_url" \
    --region "$region"

echo "DynamoDB table creation finished."
