
VOC_FILE = 'swedish_vocs.csv'
LOG = 'previous.csv'

VOICE_CMD = "say -v Alva "
VOICE_ENABLED = true

println "### Welcome back to Vocabulary Trainer ###"
if(!VOICE_ENABLED)
    println "### Disabling voice ###"
db = VocabularyDatabase.loadFromVocFile(VOC_FILE)
db.loadPreviousResults(LOG)
println "### Successfully loaded ${db.size()} entries ###"
println "### ${db.positiveRated()} entries are rated positively, ${db.countNews()} are new. ###"
println "### Your overall record is ${db.overallRecord()}. ###"
cont = true
reader = System.in.newReader()
stats = new Stats()
while(cont) {

    selection = db.nextQuestion()
    nextQuestion = selection['q']
    println "Q (${selection['reason']}): ${nextQuestion.q}"
    print "> "
    answer = reader.readLine().trim()

    if(VOICE_ENABLED)
        (VOICE_CMD + "'$nextQuestion.a'").execute().waitFor()

    result = new ComparisonResult(expected: nextQuestion.a, actual: answer)
    if(result.isCorrect()) {
        println "### Correct ###"
        stats.correct++
    } else {
        println("### Incorrect. Correct answer would have been $result ###")
        stats.incorrect++
    }

    db.answered(nextQuestion, answer, result.isCorrect(), LOG)

    println "### Type <ENTER> to continue, 'bye' to quit ###"
    if(reader.readLine().trim() == 'bye')
        cont = false

}

cor = stats.correct
total = stats.total()
println "### Bye! You answered $total questions, with $cor correct tries. That's ${cor/total * 100}%. ###"
