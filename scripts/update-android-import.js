var fs = require('fs')
const args = process.argv.slice(2)
 
const FILE = args[0]
const REPLACE = args[1]
const REPLACE_WITH = args[2]

fs.readFile(FILE, 'utf8', (err, data) => {
    if (err) {
        console.error(`Updating import failed ${err}`)
        process.exit(1)
    }
    var result = data.replace(REPLACE, REPLACE_WITH);
    fs.writeFile(FILE, result, 'utf8', function (err) {
        if (err) {
            console.error(err)
            process.exit(1)
        }
        console.log('Android import statement updated successfully.')
    });
})
