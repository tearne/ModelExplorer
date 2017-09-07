var Plot = function(parentEl, key) {
    console.log("Key: "+key)
    data = chartData.dataRaw[key]
    console.log(" data "+data)
    let lineData = data.values

  //let lineData = chartData.inputs.beta.values

//  parentEl.select('.chart-title')
//    .text(key + ' data from generation ' + gen)

  let bbox = parentEl.select('.chart')
          .node().getBoundingClientRect()

  let margin = {top: 20, right: 30, bottom: 30, left: 60},
      width = bbox.width - margin.left - margin.right,
      height = bbox.height - margin.top - margin.bottom

  var x = d3.scaleLinear()
          .domain(d3.extent(
            lineData.map(d => d[0])
          ))
          .range([0, width])

  var y = d3.scaleLinear()
          .domain(d3.extent(
            lineData.map(d => d[1])
          ))
          .range([height, 0])

var line = d3.line()
           .x((d) => { return x(d[0])})
           .y((d) => { return y(d[1])})

  let svg = parentEl.select("svg")
    .attr('width', bbox.width)
    .attr('height', bbox.height)
    .append('g')
    .attr("transform",
          "translate(" + margin.left + "," + margin.top + ")")

   function brushed() {
     var extent = d3.event.selection.map(x.invert, x);
      svg.classed("selected", function(d) { return extent[0] <= d[0] && d[0] <= extent[1]; });
      }


function brushcentered() {
  var dx = x(1) - x(0), // Use a fixed width when recentering.
      cx = d3.mouse(this)[0],
      x0 = cx - dx / 2,
      x1 = cx + dx / 2;
  d3.select(this.parentNode).call(brush.move, x1 > width ? [width - dx, width] : x0 < 0 ? [0, dx] : [x0, x1]);
}

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


    var brush = d3.brushX()
        .extent([[0, 0], [width, height]])
        .on("start brush", brushed);

   svg.append("g")
       .call(brush)
       .call(brush.move, [3, 5].map(x))
     .selectAll(".overlay")
       .each(function(d) { d.type = "selection"; }) // Treat overlay interaction as move.
       .on("mousedown touchstart", brushcentered); // Recenter before brushing.
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


