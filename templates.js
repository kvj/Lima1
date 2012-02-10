var wfront = {
	"defaults": {
		"title": "Front page"
	},
	"direct": true,
	"flow": [
		{"type": "title", "name": "Title:", "edit":"@:title"},
		{"type": "hr"},
		{"type": "calendar"},
		{"type": "title1", "name": "Contact information"},
		{
			"border": 1,
			"delimiter": 1,
			"grow": "no",
			"flow": [
				{
					"type": "cols",
					"size": [0.2, 0.8],
					"flow": [
						{"type": "title3", "name": "Name", "bg": 1},
						{"type": "text", "edit": "@:name"}
					]
				},
				{
					"type": "cols",
					"size": [0.2, 0.8],
					"flow": [
						{"type": "title3", "name": "Address", "bg": 1},
						{
							"type": "list",
							"delimiter": 2,
							"grow": "no",
							"area": "address",
							"item": {
								"flow": [
									{"type": "text", "edit": "@:text"}
								]
							}
						}
					]
				},
				{
					"type": "cols",
					"size": [0.2, 0.8],
					"flow": [
						{"type": "title3", "name": "Telephone", "bg": 1},
						{
							"delimiter": 1,
							"flow": [
								{
									"type": "cols",
									"size": [0.5, 0.5],
									"flow": [
										{"type": "text", "edit": "@:work_phone", "title": "Work", "line": 1},
										{"type": "text", "edit": "@:fax_phone", "title": "Fax"}
									]
								},
								{
									"type": "cols",
									"size": [0.5, 0.5],
									"flow": [
										{"type": "text", "edit": "@:cell_phone", "title": "Mobile", "line": 1},
										{"type": "text", "edit": "@:home_phone", "title": "Home"}
									]
								},
								{"type": "text", "edit": "@:other_phone", "title": "Other"}
							]
						}
					]
				},
				{
					"type": "cols",
					"size": [0.2, 0.8],
					"flow": [
						{"type": "title3", "name": "Messenger", "bg": 1},
						{"type": "text", "edit": "@:im"}
					]
				},
				{
					"type": "cols",
					"size": [0.2, 0.8],
					"flow": [
						{"type": "title3", "name": "Website", "bg": 1},
						{"type": "text", "edit": "@:url"}
					]
				},
				{
					"type": "cols",
					"size": [0.2, 0.8],
					"flow": [
						{"type": "title3", "name": "", "bg": 1},
						{"type": "text", "edit": "@:other"}
					]
				}
			]
		},
		{
			"type": "list",
			"border": 1,
			"delimiter": 2,
			"area": "main",
			"item": {
				"flow": [
					{"type": "text", "edit": "@:text"}
				]
			}
		}
	]
}

var wobjective = {
	"defaults": {
		"title": "Objective"
	},
	"direct": true,
	"flow": [
		{"type": "title", "name": "Objectives", "edit":"@:title"},
		{"type": "hr"},
		{"type": "title2", "name": "Objective", "edit": "@:objective"},
		{
			"border": 1,
			"delimiter": 1,
			"grow": "no",
			"flow": [
				{
					"type": "cols",
					"size": [0.15, 0.85],
					"flow": [
						{"type": "title3", "name": "Description", "bg": 1},
						{"type": "text", "edit": "@:desc"}
					]
				},
				{
					"type": "cols",
					"size": [0.15, 0.85],
					"flow": [
						{"type": "title3", "name": "Benefits", "bg": 1},
						{"type": "text", "edit": "@:benefits"}
					]
					
				},
				{
					"type": "cols",
					"size": [0.15, 0.85],
					"flow": [
						{"type": "title3", "name": "Challenges", "bg": 1},
						{"type": "text", "edit": "@:challenges"}
					]
				}
			]
		},
		{"type": "header", "size": [0.15, 0.75, 0.15], "flow": ["", "Step", "Target"]},
		{
			"type": "list", 
			"area": "main", 
			"border": 1,
			"delimiter": 1,
			"item": {
				"flow": [
					{"type": "cols", "size": [0.15, 0.75, 0.15], "flow": [
						{"type": "check", "edit": "@:done", "bg": 1},
						{"type": "text", "edit": "@:text"},
						{"type": "date", "edit": "@:due", "bg": 1}
					]}
				]
			}
		},
		{
			"border": 1,
			"grow": "no",
			"flow": [
				{
					"type": "cols",
					"size": [0.15, 0.85],
					"flow": [
						{"type": "title3", "name": "Outcome", "bg": 1},
						{"type": "text", "edit": "@:outcome"}
					]
				}
			]
		}
	]
}

var wcombinedactions = {
	"defaults": {
		"title": "Combined Actions"
	},
	"direct": true,
	"flow": [
		{"type": "title", "name": "Combined Actions", "edit":"@:title"},
		{"type": "hr"},
		{"type": "cols", "size": [0.48, 0.48], "space": 0.04, "flow": [
			{"flow": [
				{"type": "title1", "name": "Actions"},
				{
					"type": "list", 
					"area": "main", 
					"border": 1,
					"delimiter": 1,
					"item": {
						"flow": [
							{"type": "cols", "size": [0.1, 0.9], "flow": [
								{"type": "check", "edit": "@:done", "bg": 1},
								{"type": "text", "edit": "@:text"}
							]}
						]
					}
				}
			]}, {"flow": [
				{"type": "title1", "name": "Waiting For"},
				{
					"type": "list",
					"area": "wfor",
					"border": 1,
					"delimiter": 1,
					"item": {
						"delimiter": 2,
						"flow": [
							{"type": "cols", "size": [0.1, 0.9], "flow": [
								{"type": "check", "edit": "@:done", "bg": 1},
								{"type": "text", "edit": "@:text"}
							]
							}, {"type": "text", "edit": "@:notes"}
						]
					}
				}
			]}
		]},
		{"type": "title1", "name": "Notes"},
		{
			"type": "list",
			"area": "notes",
			"border": 1,
			"delimiter": 1,
			"grow": "no",
			"item": {
				"flow": [
					{"type": "text", "edit": "@:text"}
				]
			}
		}
	]	
}

var wcombinedactions_h = {
	"defaults": {
		"title": "Combined Actions"
	},
	"direct": true,
	"flow": [
		{"type": "title", "name": "Combined Actions", "edit":"@:title"},
		{"type": "hr"},
		{"type": "title1", "name": "Actions"},
		{
			"type": "list", 
			"area": "main", 
			"border": 1,
			"delimiter": 1,
			"item": {
				"flow": [
					{"type": "cols", "size": [0.05, 0.95], "flow": [
						{"type": "check", "edit": "@:done", "bg": 1},
						{"type": "text", "edit": "@:text"}
					]}
				]
			}
		},
		{"type": "title1", "name": "Waiting For"},
		{
			"type": "list",
			"area": "wfor",
			"grow": "no",
			"border": 1,
			"delimiter": 1,
			"item": {
				"delimiter": 2,
				"flow": [
					{"type": "cols", "size": [0.05, 0.95], "flow": [
						{"type": "check", "edit": "@:done", "bg": 1},
						{"type": "text", "edit": "@:text"}
					]
					}, {"type": "text", "edit": "@:notes"}
				]
			}
		},
		{"type": "title1", "name": "Notes"},
		{
			"type": "list",
			"area": "notes",
			"border": 1,
			"delimiter": 1,
			"grow": "no",
			"item": {
				"flow": [
					{"type": "text", "edit": "@:text"}
				]
			}
		}
	]	
}

var wactions = {
	"defaults": {
		"title": "Actions"
	},
	"direct": true,
	"flow": [
		{"type": "title", "name": "Actions", "edit":"@:title"},
		{"type": "hr"},
		{
			"type": "list",
			"area": "main",
			"item": {
				"border": 2,
				"delimiter": 1,
				"flow": [
					{"type": "cols", "size": [0.05, 0.8, 0.15], "flow": [
						{"type": "check", "edit": "@:done"},
						{"type": "text", "edit": "@:text", "line": 1},
						{"type": "date", "edit": "@:due"}
					]},
					{"type": "cols", "size": [0.05, 0.95], "flow": [
						{"type": "mark", "edit": "@:mark"},
						{"type": "text", "edit": "@:notes"}
					]}
				]
			}
		}
	]	
}

var wsimpleactions = {
	"defaults": {
		"title": "To-Do"
	},
	"direct": true,
	"flow": [
		{"type": "title", "name": "Actions", "edit":"@:title"},
		{"type": "hr"},
		{
			"type": "list",
			"area": "main",
			"item": {
				"border": 1,
				"flow": [
					{"type": "cols", "size": [0.05, 0.8, 0.15], "flow": [
						{"type": "check", "edit": "@:done"},
						{"type": "text", "edit": "@:text", "line": 1},
						{"type": "date", "edit": "@:due"}
					]}
				]
			}
		}
	]
}

var wnotes = {
	"defaults": {
		"title": "Notes"
	},
	"direct": true,
	"flow": [
		{"type": "title", "name": "Notes", "edit":"@:title"},
		{"type": "hr"},
		{
			"type": "list",
			"border": 2,
			"delimiter": 2,
			"area": "main",
			"item": {
				"flow": [
					{"type": "text", "edit": "@:text"}
				]
			}
		}
	]	
}

var w13 = {
	"code": "w13:${dt:(e1)}",
	"protocol": {
		"dt": {
			"e": [1, 2, 3]
		}
	},
	"defaults": {
		"title": "${dt:(e1)yyyy}: ${dt:(e1)MM/dd} - ${dt:(e3)MM/dd}",
		"dt": "${dt:(e1)}"
	},
	"flow": [
		{"type": "title", "name": "Week ${dt:ww}/${dt:yyyy}", "edit":"@:title"},
		{"type": "hr"},
		{"type": "title1", "name": "${dt:(e1)dd} Monday"},
		{"type": "cols", "size": [0.55, 0.5], "space": 0.05, "flow": [
			{
				"type": "list", 
				"area": "d1",
				"border": 1,
				"delimiter": 1,
				"defaults": {"due": "${dt:(e1)}"},
				"item": {
					"flow": [
						{"type": "cols", "size": [0.15, 0.85], "flow": [
							{"type": "time", "edit": "@:time", "bg": 1},
							{"type": "text", "edit": "@:text"}
						]}
					]
				}
			}, {
				"flow": [
					{
						"type": "list", 
						"area": "t1",
						"border": 1,
						"delimiter": 1,
						"defaults": {"due": "${dt:(e1)}"},
						"item": {
							"flow": [
								{"type": "cols", "size": [0.1, 0.9], "flow": [
									{"type": "check", "edit": "@:done"},
									{"type": "text", "edit": "@:text"}
								]}
							]
						}
					}, {
						"type": "list",
						"area": "n1",
						"border": 2,
						"delimiter": 2,
						"item": {
							"flow": [
								{"type": "text", "edit": "@:text"}
							]
						}
					}
				]
			}
		]},
		{"type": "title1", "name": "${dt:(e2)dd} Tuesday"},
		{"type": "cols", "size": [0.55, 0.5], "space": 0.05, "flow": [
			{
				"type": "list", 
				"area": "d2",
				"border": 1,
				"delimiter": 1,
				"defaults": {"due": "${dt:(e2)}"},
				"item": {
					"flow": [
						{"type": "cols", "size": [0.15, 0.85], "flow": [
							{"type": "time", "edit": "@:time", "bg": 1},
							{"type": "text", "edit": "@:text"}
						]}
					]
				}
			}, {
				"flow": [
					{
						"type": "list", 
						"area": "t2",
						"border": 1,
						"delimiter": 1,
						"defaults": {"due": "${dt:(e2)}"},
						"item": {
							"flow": [
								{"type": "cols", "size": [0.1, 0.9], "flow": [
									{"type": "check", "edit": "@:done"},
									{"type": "text", "edit": "@:text"}
								]}
							]
						}
					}, {
						"type": "list",
						"area": "n2",
						"border": 2,
						"delimiter": 2,
						"item": {
							"flow": [
								{"type": "text", "edit": "@:text"}
							]
						}
					}
				]
			}
		]},
		{"type": "title1", "name": "${dt:(e3)dd} Wednesday"},
		{"type": "cols", "size": [0.55, 0.5], "space": 0.05, "flow": [
			{
				"type": "list", 
				"area": "d3",
				"border": 1,
				"delimiter": 1,
				"defaults": {"due": "${dt:(e3)}"},
				"item": {
					"flow": [
						{"type": "cols", "size": [0.15, 0.85], "flow": [
							{"type": "time", "edit": "@:time", "bg": 1},
							{"type": "text", "edit": "@:text"}
						]}
					]
				}
			}, {
				"flow": [
					{
						"type": "list", 
						"area": "t3",
						"border": 1,
						"delimiter": 1,
						"defaults": {"due": "${dt:(e3)}"},
						"item": {
							"flow": [
								{"type": "cols", "size": [0.1, 0.9], "flow": [
									{"type": "check", "edit": "@:done"},
									{"type": "text", "edit": "@:text"}
								]}
							]
						}
					}, {
						"type": "list",
						"area": "n3",
						"border": 2,
						"delimiter": 2,
						"item": {
							"flow": [
								{"type": "text", "edit": "@:text"}
							]
						}
					}
				]
			}
		]}
	]
}

var w47 = {
	"code": "w47:${dt:(e1)}",
	"protocol": {
		"dt": {
			"e": [4, 5, 6, 0]
		}
	},
	"defaults": {
		"title": "${dt:(e4)yyyy}: ${dt:(e4)MM/dd} - ${dt:(e7)MM/dd}",
		"dt": "${dt:(e4)}"
	},
	"flow": [
		{"type": "title", "name": "Week ${dt:ww}/${dt:yyyy}", "edit":"@:title"},
		{"type": "hr"},
		{"type": "title1", "name": "${dt:(e4)dd} Thursday"},
		{"type": "cols", "size": [0.55, 0.5], "space": 0.05, "flow": [
			{
				"type": "list", 
				"area": "d1",
				"border": 1,
				"delimiter": 1,
				"defaults": {"due": "${dt:(e4)}"},
				"item": {
					"flow": [
						{"type": "cols", "size": [0.15, 0.85], "flow": [
							{"type": "time", "edit": "@:time", "bg": 1},
							{"type": "text", "edit": "@:text"}
						]}
					]
				}
			}, {
				"flow": [
					{
						"type": "list", 
						"area": "t1",
						"border": 1,
						"delimiter": 1,
						"defaults": {"due": "${dt:(e4)}"},
						"item": {
							"flow": [
								{"type": "cols", "size": [0.1, 0.9], "flow": [
									{"type": "check", "edit": "@:done"},
									{"type": "text", "edit": "@:text"}
								]}
							]
						}
					}, {
						"type": "list",
						"area": "n1",
						"border": 2,
						"delimiter": 2,
						"item": {
							"flow": [
								{"type": "text", "edit": "@:text"}
							]
						}
					}
				]
			}
		]},
		{"type": "title1", "name": "${dt:(e5)dd} Friday"},
		{"type": "cols", "size": [0.55, 0.5], "space": 0.05, "flow": [
			{
				"type": "list", 
				"area": "d2",
				"border": 1,
				"delimiter": 1,
				"defaults": {"due": "${dt:(e5)}"},
				"item": {
					"flow": [
						{"type": "cols", "size": [0.15, 0.85], "flow": [
							{"type": "time", "edit": "@:time", "bg": 1},
							{"type": "text", "edit": "@:text"}
						]}
					]
				}
			}, {
				"flow": [
					{
						"type": "list", 
						"area": "t2",
						"border": 1,
						"delimiter": 1,
						"defaults": {"due": "${dt:(e5)}"},
						"item": {
						"flow": [
							{"type": "cols", "size": [0.1, 0.9], "flow": [
								{"type": "check", "edit": "@:done"},
								{"type": "text", "edit": "@:text"}
							]}
						]
						}
					}, {
						"type": "list",
						"area": "n2",
						"border": 2,
						"delimiter": 2,
						"item": {
							"flow": [
								{"type": "text", "edit": "@:text"}
							]
						}
					}
				]
			}
		]},
		{"type": "title1", "name": "${dt:(e6)dd}, ${dt:(e7)dd} Weekend"},
		{"type": "cols", "size": [0.55, 0.5], "space": 0.05, "flow": [
			{
				"type": "list", 
				"area": "d3",
				"border": 1,
				"delimiter": 1,
				"item": {
					"flow": [
						{"type": "cols", "size": [0.15, 0.85], "flow": [
							{"type": "time", "edit": "@:time", "bg": 1},
							{"type": "text", "edit": "@:text"}
						]}
					]
				}
			}, {
				"flow": [
					{
						"type": "list", 
						"area": "t3",
						"border": 1,
						"delimiter": 1,
						"item": {
							"flow": [
								{"type": "cols", "size": [0.1, 0.9], "flow": [
									{"type": "check", "edit": "@:done"},
									{"type": "text", "edit": "@:text"}
								]}
							]
						}
					}, {
						"type": "list",
						"area": "n3",
						"border": 2,
						"delimiter": 2,
						"item": {
							"flow": [
								{"type": "text", "edit": "@:text"}
							]
						}
					}
				]
			}
		]}
	]
}
