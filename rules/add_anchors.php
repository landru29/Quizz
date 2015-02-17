#!/usr/bin/php
 <?php

class transformer {

    public $counter = array();
    public $body = false;
    public $section = array();
    public $currentLevel=0;
    public $requestedAttr='';

    public function resetSubCounter($index) {
        for ($i = $index+1; $i<10; $i++) {
            $this->counter[$i] = 0;
        }
    }
    
    public function getFileName() {
        return sprintf('%02d_%02d', $this->counter[1], $this->counter[2]);
    }

    public function counterStr() {
        $temp = $this->counter;
        unset ($temp[0]);
        for($i=9; $i>0; $i--) {
            if ($temp[$i] == 0) {
                unset ($temp[$i]);
            } else {
                return implode('_', $temp);
            }
        }
        return implode('_', $temp);
    }
    
    public function exportFiles() {
        foreach($this->section as $name=>$data) {
            if (strlen($data)>0) {
                file_put_contents($name . '.html', $data);
            }
        }
    }


    public function __construct($data) {
        $this->resetSubCounter(-1);
        for ($i=0; $i<strlen($data); $i++) {
            if (!isset($this->section[$this->getFileName()])) {
                $this->section[$this->getFileName()] = '';
            }
            if (preg_match ('#<h(\d)>#' , substr($data, $i, 4) , $matches)) {
                $level = $matches[1];
                $this->resetSubCounter($level);
                $this->counter[$level]++;
                if (!isset($this->section[$this->getFileName()])) {
                    $this->section[$this->getFileName()] = '';
                }
                if (($level == 3) && ($this->section[$this->getFileName()]=='')) {
                    echo '<div ng-show="display[\'' . $this->getFileName() . '\']" data-ng-bind-html="data[\'' . $this->getFileName() . "']\"></div>\n";
                }
                if ($level>2) {
                    $this->section[$this->getFileName()] .= '<a id="section_' . $this->counterStr() . '"></a>';
                } else {
                    echo '<a id="section_' . $this->counterStr() . '"></a>';
                }
                if ($level==2) {
                    $this->requestedAttr = ' ng-click="toggle(\'' . $this->getFileName() . '\')" class="clickable"';
                }
                $this->currentLevel = $level;
            }
            if (preg_match ('#<\/body>#' , substr($data, $i, 7) , $matches)) {
                $this->body = false;
            }
            if (($i>6) && (preg_match ('#<body>#' , substr($data, $i-6, 6) , $matches))) {
                $this->body = true;
            }
            if ((!$this->body) || ($this->currentLevel<3)) {
                if (($data[$i] == '>') && (strlen($this->requestedAttr)>0)) {
                    echo $this->requestedAttr;
                    $this->requestedAttr = '';
                }
                echo $data[$i];
            } else {
                $this->section[$this->getFileName()] .= $data[$i];
            }
        }
        $this->exportFiles();
    }
}

 if (count($argv)>1) {

    $data = file_get_contents($argv[1]);
    new transformer($data);
 }
