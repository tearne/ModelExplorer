

var url = "localhost:9000/data";

d3.json(url, function (json) {
    console.log("You got data: "+json)
});