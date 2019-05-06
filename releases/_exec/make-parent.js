var fs = require('fs'), xml2js = require('xml2js');


var parser = new xml2js.Parser();
var xml = fs.readFileSync(__dirname + '/../../pom.xml', 'utf-8');
parser.parseString(xml, function(err, result) {

	let v = result.project.version[0];
	let dir = __dirname + "/../cc/creamcookie/ccsframe4j/starter-parent/" + v;

	fs.mkdirSync(dir, { recursive: true });

	let output = fs.readFileSync(__dirname + "/tpl.xml", "utf8");
	output = output.replace(/\$version\$/g, v);

	fs.writeFileSync(`${dir}/starter-parent-${v}.pom`, output, {});

});
