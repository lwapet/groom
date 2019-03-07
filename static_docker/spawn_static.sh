task() {
	 ./run.sh "$1"
}

N=8
(
while read p; do
	((i=i%N)); ((i++==0)) && wait
	task "$p" &
done <shas.txt

)
