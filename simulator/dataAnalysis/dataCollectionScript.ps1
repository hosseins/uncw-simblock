cd ../..
for($i = 0; $i -le 20; $i++)
{
    ./gradlew run
    mv simulator/src/dist/output/output.json simulator/dataAnalysis/jsonOutput/output${i}.json
    mv simulator/src/dist/output/blockList.txt simulator/dataAnalysis/blockOutput/blockList${i}.json

}
