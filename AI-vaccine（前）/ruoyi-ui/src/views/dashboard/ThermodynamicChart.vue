<template>
  <div :class="className" :style="{height:height,width:width}" />
</template>

<script>
import * as echarts from 'echarts';
require('echarts/theme/macarons') // echarts theme
import resize from './mixins/resize'

export default {
  mixins: [resize],
  props: {
    className: {
      type: String,
      default: 'chart'
    },
    width: {
      type: String,
      default: '100%'
    },
    height: {
      type: String,
      default: '350px'
    },
    autoResize: {
      type: Boolean,
      default: true
    },
    chartData: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      chart: null,
    }
  },
  watch: {
    chartData: {
      deep: true,
      handler(val) {
        this.setOptions(val)
      }
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initChart()
    })
  },
  beforeDestroy() {
    if (!this.chart) {
      return
    }
    this.chart.dispose()
    this.chart = null
  },
  methods: {
    initChart() {
      this.chart = echarts.init(this.$el, 'macarons')
      this.setOptions(this.chartData)
    },
    setOptions({ xdata, ydata, data, max, min} = {}) {
      this.chart.setOption({
        tooltip: {
          position: 'top'
        },
        toolbox: {
          show: true,
          orient: "top",
          left: "right",
          top: "left",
          feature: {
            saveAsImage: { show: true },  // 保存图表
          },
        },
        grid: {
          height: '50%',
          top: '10%',
          left: '12%'
        },
        xAxis: {
          type: 'category',
          data: xdata,
          splitArea: {
            show: true
          },
          axisLine: {
            lineStyle: {
              color: '#666', // 设置坐标轴颜色
            }
          },
          axisLabel: {
            show: true,
            textStyle: {
              color: '#000',
            },
            interval: 0,
            // rotate: 15
          },
        },
        yAxis:{
          type: 'category',
          data: ydata,
          splitArea: {
            show: true
          },
          axisLine: {
            lineStyle: {
              color: '#666', // 设置坐标轴颜色
            }
          },
          axisLabel: {
            show: true,
            textStyle: {
              color: '#000',
            },
            interval: 0,
            // rotate: 15
          },
        },
        visualMap: {
          min: min,
          max: max,
          precision: 2,
          calculable: true,
          orient: 'horizontal',
          left: 'center',
          bottom: '15%',
          inRange: {
            color: ['#f6efa6', '#d88273', '#bf444c']
          },
        },
        series:[
          {
            name: '值',
            type: 'heatmap',
            data: data,
            label: {
              show: false,
              textStyle: {
                color: "#fff"
              }
            },
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowColor:'rgba(0,0,0, 0.5)'
              }
            },
          }
        ]
      })
    }
  }
}
</script>
