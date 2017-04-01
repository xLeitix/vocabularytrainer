
VOC_FILE = 'swedish_vocs.csv'
LOG = 'previous.csv'

println "### Welcome back to Vocabulary Trainer ###"
db = VocabularyDatabase.loadFromVocFile(VOC_FILE)
db.loadPreviousResults(LOG)
println "### Successfully loaded ${db.size()} entries ###"

cont = true
reader = System.in.newReader()
stats = new Stats()
while(cont) {

    nextQuestion = db.nextQuestion()
    println "Q: ${nextQuestion.q}"
    print "> "
    answer = reader.readLine().trim()

    correct = (answer.equalsIgnoreCase(nextQuestion.a))
    if(correct) {
        println "### Correct ###"
        stats.correct++
    } else {
        println "### Incorrect. Correct answer would have been ${nextQuestion.a} ###"
        stats.incorrect++
    }

    db.answered(nextQuestion, answer, correct, LOG)

    println "### Type <ENTER> to continue, 'bye' to quit ###"
    if(reader.readLine().trim() == 'bye')
        cont = false

}

println "### Bye! You answered ${stats.total()} questions, with ${stats.correct} correct tries ###"