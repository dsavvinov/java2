import pandas as pd
import numpy as np
import plotly as py
import plotly.graph_objs as go

queries = ['client-time', 'query-time', 'running-time', 'server-overhead']
variables = ['array-size', 'clients-amount', 'delay']
architectures = ['1-raw-thread-tcp', '2-cached-pool-tcp', '3-non-blocking-tcp', '4-separate-connection-tcp',
                 '5-async-tcp', '6-raw-thread-udp', '7-fixed-pool-udp']

def prettyPrintArch(folderName):
    words = folderName.split("-")[1:]
    words[0] = words[0].title()
    return " ".join(words)

def prettyPrint(name):
    words = name.split("-")
    words[0] = words[0].title()
    return " ".join(words)

for var in variables:
    for q in queries:
        x = prettyPrint(var)
        y = prettyPrint(q)
        traces = []
        for arch in architectures:
            prettyArch = prettyPrintArch(arch)
            df = pd.read_csv(var + '/' + arch + '/' + q + '.csv')
            trace = go.Scatter(
                x = df[x],
                y = df[' ' + y],
                mode = 'lines+markers' ,
                name = prettyArch
            )
            traces.append(trace)
        layout = go.Layout(
            title=y + ' with variable ' + x,
            xaxis=dict(
                title=x,
                titlefont=dict(
                    family='Courier New, monospace',
                    size=18,
                    color='#7f7f7f'
                )
            ),
            yaxis=dict(
                title=y + ", ms",
                titlefont=dict(
                    family='Courier New, monospace',
                    size=18,
                    color='#7f7f7f'
                )
            )
        )
        fig = go.Figure(data=traces, layout=layout)
        py.offline.plot(fig, show_link=False, auto_open=False, filename=var + '-' + q + '.html')
