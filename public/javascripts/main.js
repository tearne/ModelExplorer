

var url = "http://localhost:9000/data";
var json

var chartData


let makeFramework = function(){

  let charts = d3.select("#charts")
        .style('width', config.WIDTH + 'px')
        .style('height', config.HEIGHT + 'px')
        .style('margin-left', config.MARGIN.left + 'px')

  // Nine key variables in a 3x3 grid
//  let chartHeight = parseInt(config.HEIGHT / 3)
//  let chartWidth = parseInt(config.WIDTH / 3)

    let chartHeight = parseInt(config.HEIGHT)
    let chartWidth = parseInt(config.WIDTH)

  charts = charts.selectAll('.chart-wrapper')
     // .data(chartData.inputs)
    .data(chartData.meta.inputNames)
    .enter()
    .append('div')
    .attr('class', 'chart-wrapper')
    .style('width', chartWidth + 'px').style('height', chartHeight + 'px')
    .attr('id', d => 'chart-' + d)
//    .style('top', (d, i) => (chartHeight * parseInt(i / 3)) + 'px' )
//    .style('left', (d, i) => (chartWidth * (i % 3)) + 'px')
    .style('top', (d, i) => (chartHeight * parseInt(i)) + 'px' )

  charts.append('div')
    .attr('class', 'chart-title')

  charts.append('div')
    .attr('class', 'chart')
    .append('svg')

  // Place main axis label (density)
  d3.select('.main-axis-label-y')
    .style('top', parseInt(config.HEIGHT/2) + 'px')
    .style('left', '-70px')
}

d3.json(url, function (err, json) {
    console.log("You got data: "+json)
    // Do d3 magic
      if(err) throw err
      chartData = json
      makeFramework()

            chartData.meta.inputNames.forEach(d => {
                          Plot(d3.select('#chart-' + d), d)
                          })

});