const FgRed = "\x1b[31m"
const FgGreen = "\x1b[32m"
const FgWhite = "\x1b[37m"

const checkMark = `${FgGreen}✓${FgWhite}`
const errorMark = `${FgRed}✕${FgWhite}`
const allGood = `${FgGreen}All good =D${FgWhite}`
const epicFail = `${FgRed}Your code sucks!${FgWhite}`

const errorMessage = "Ooops, you are a bad developer..."

const describe = (message, callback) => {
    console.log(message)
    try {
        callback()
        console.log(allGood)
    } catch(e) {
        if(e.message !== errorMessage) throw e
        console.log(epicFail)
    }
}

const it = (message, assertions) => {
    try{
        assertions()
        console.log(checkMark, message)
    } catch(e){
        console.log(errorMark, message)
        throw e
    }
}

const expects = thing => ({
    toEqual: other => assert(other.equals ? other.equals(thing) : other == thing),
    toBe: other => assert(other === thing),
    toBeTruthy: () => assert(!!!!thing),
    toBeFalsey: () => assert(!!!thing)
})

function assert(assertion){
    if(!assertion) throw new Error(errorMessage)
}

module.exports = {
    describe,
    it,
    expects
}