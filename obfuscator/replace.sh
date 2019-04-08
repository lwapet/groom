find "$1" -type f -exec sed -i '' -e 's/com\.lock\.app/com\.michel\.delpech/' {} \;
find "$1" -type f -exec sed -i '' -e 's#Lcom/lock/app#Lcom/michel/delpech#' {} \;

