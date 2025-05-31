{{/*
Expand the name of the chart.
*/}}
{{- define "twist-product.name" -}}
{{- default .Chart.Name .Values.nameOverride | lower | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "twist-product.fullname" -}}
{{- if .Values.fullnameOverride -}}
{{- .Values.fullnameOverride | lower | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- $name := default .Chart.Name .Values.nameOverride | lower -}}
{{- if contains $name .Release.Name -}}
{{- .Release.Name | lower | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- printf "%s-%s" .Release.Name $name | lower | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- end -}}
{{- end -}}

{{/*
Create chart's version and application version as part of the labels
*/}}
{{- define "twist-product.version" -}}
{{- printf "%s-%s" .Chart.Version .Chart.AppVersion -}}
{{- end -}}

{{/*
Create the label for the selector
*/}}
{{- define "twist-product.selectorLabels" -}}
app.kubernetes.io/name: {{ include "twist-product.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end -}}

{{/*
Create the labels
*/}}
{{- define "twist-product.labels" -}}
{{ include "twist-product.selectorLabels" . }}
helm.sh/chart: {{ include "twist-product.chart" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end -}}

{{/*
Create the name of the service account to use
*/}}
{{- define "twist-product.serviceAccountName" -}}
{{- if .Values.serviceAccount.create -}}
{{- default (include "twist-product.fullname" .) .Values.serviceAccount.name -}}
{{- else -}}
{{- default "default" .Values.serviceAccount.name -}}
{{- end -}}
{{- end -}}

{{/*
Common labels
*/}}
{{- define "twist-product.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- end -}}