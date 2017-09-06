var Plot = function(parentEl, key) {

    data = chartData.inputs[key]
    let lineData = data.values

  //let lineData = chartData.inputs.beta.values

//  parentEl.select('.chart-title')
//    .text(key + ' data from generation ' + gen)

  let bbox = parentEl.select('.chart')
          .node().getBoundingClientRect()

  let margin = {top: 20, right: 30, bottom: 30, left: 60},
      width = bbox.width - margin.left - margin.right,
      height = bbox.height - margin.top - margin.bottom

  let x = d3.scaleLinear()
          .domain(d3.extent(
            lineData.map(d => d[0])
          ))
          .range([0, width])

  let y = d3.scaleLinear()
          .domain(d3.extent(
            lineData.map(d => d[1])
          ))
          .range([height, 0])

  let line = d3.line()
        .x((d) => { return x(d[0])})
        .y((d) => { return y(d[1])})

  let svg = parentEl.select("svg")
    .attr('width', bbox.width)
    .attr('height', bbox.height)
    .append('g')
    .attr("transform",
          "translate(" + margin.left + "," + margin.top + ")")

    svg.append('path')
      .classed('line', true)
      .datum(lineData)
      .attr('stroke', 'blue')
      .attr('d', line)
    //  .attr('stroke-width', (d) => d3.sum(d.map{d => d[0]}))

    // Draw the SVG axes using D3's utility functions
      svg.append("g")
      .attr("class", "axis axis--x")
      .attr("transform", "translate(0," + height +")")
      .call(d3.axisBottom(x))

      svg.append("g")
        .attr("class", "axis axis--y")
        .call(d3.axisLeft(y))

        // y axis label
        svg.append("text")
          .attr("class", "axis-label")
          .attr("transform", "translate(-40," + height/2 + ")," + "rotate(-90)")
          .text("Plot")
}

var KDEPlot = function(parentEl, key){

  data = chartData.inputs[key].values
  //let lineData = data.values

  //data = chartData[key]
  // Set chart title
  parentEl.select('.chart-title')
    .text(key)
  // Set chart dimensions
  let bbox = parentEl.select('.chart')
        .node().getBoundingClientRect()

  let svg = parentEl.select('svg')
        .attr('width', bbox.width)
        .attr('height', bbox.height)

  let margin = {top: 20, right: 30, bottom: 30, left: 40},
      width = bbox.width - margin.left - margin.right,
      height = bbox.height - margin.top - margin.bottom
  // Create axes scales
  let x = d3.scaleLinear()
        .domain(d3.extent(
          // flatten turns [[1], [2], [3]] into [1, 2, 3]
          _.flatten(data.map(d => d.values).map(d => d.map(d => d[0])))
        ))
        .range([0, width])

  let y = d3.scaleLinear()
        .domain(d3.extent(
          _.flatten(data.map(d => d.values).map(d => d.map(d => d[1])))
        ))
        .range([height, 0])

  // A color scale for the lines + legend
  // Name domain ["Gen001", "Gen002", "Gen002" ... ]  mapped to
  // Color range ["#1f77b4", "#aec7e8", "#ff7f0e", ...]
  let color = d3.scaleOrdinal()
        .domain(data.map(d => d.name))
        .range(d3[config.colorScale])

  let densityLine = d3.line()
        .x((d) => { return x(d[0])})
        .y((d) => { return y(d[1])})

  svg = parentEl.select("svg")
        .append('g')
        .attr("transform",
              "translate(" + margin.left + "," + margin.top + ")")
  // Make chart axes
  svg.append("g")
    .attr("class", "axis axis--x")
    .attr("transform", "translate(0," + height + ")")
    .call(d3.axisBottom(x).ticks(4))

  svg.append("g")
    .attr("class", "axis axis--y")
    .call(d3.axisLeft(y))

  // Add chart lines
  let lines = svg.selectAll('.line')
    .data(data)

  lines.enter()
    .append('path')
    .classed('line', true)
    .merge(lines)
    .attr('id', d => d.name)
    .attr('stroke', (d) => color(d.name))
    .attr('d', d => densityLine(d.points))

}


