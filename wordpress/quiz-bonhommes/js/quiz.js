/**
 * Created by cyrille on 10/04/15.
 */

var QuizBonhomme = (function(){
  var QuizBonhommeObj = function(target) {
    this.container = target;
  };

  QuizBonhommeObj.prototype = {
    newQuestion: function() {
      this.container.find('input:radio,input:checkbox').prop('disabled', true);
      var _self = this;
      Parse.Cloud.run('randomQuestions', {count:1}, {
        success: function (result) {
          _self.container.html('');
          var question = result.data[0];
          _self.buildQuestion(question);
          _self.addButton('Valider', function(){
            var answer = _self.collectAnswers(_self.container.find('ul'));
            _self.getAnswer(question.objectId, answer);
          });
        },
        error: function (error) {
          console.log('The Quiz server is not reachable');
        }
      });
    },
    buildQuestion: function(question) {
      this.container.append('<span class="quiz-question">' + this.markItDown(question.text) + '</span>');
      if (question.image) {
        this.container.append('<img class="quiz-picture" src="' + question.image + '"/>');
      }
      var form = jQuery('<form id="choices"></form>');
      this.container.append(form);
      var choices = jQuery('<ul></ul>');
      form.append(choices);
      for(var i in question.choices) {
        if (question.multiAnswer) {
          choices.append('<li><input type="checkbox" name="choices" value="' + question.choices[i].objectId + '">' + question.choices[i].text + '</input></li>');
        } else {
          choices.append('<li><input type="radio" name="choices" value="' + question.choices[i].objectId + '">' + question.choices[i].text + '</input></li>');
        }
      }
    },
    buildAnswer: function(question) {
      var _self=this;
      var block = jQuery('<div class="quiz-answer ' + (question.check ? 'right' : 'wrong') + '"></div>');
      this.container.append(block);
      block.append('<span class="quiz-question">' + this.markItDown(question.text) + '</span>');
      var choices = jQuery('<ul></ul>');
      block.append(choices);
      for (var i in question.choices) {
        var thisChoice = question.choices[i];
        var classe = 'quiz-check-empty';
        if (thisChoice.answered === thisChoice.check) {
          classe = 'quiz-check-right';
        } else if ((thisChoice.answered === true) && (thisChoice.check === false)) {
          classe = 'quiz-check-wrong';
        }
        choices.append('<li><span class="' + classe + '">' + thisChoice.text + '</span></li>');
      }
      var helpButton = jQuery('<span class="quiz-help"></span>');
      /*block.append(helpButton);
      helpButton.on('click', function(){
        _self.showExplaination(question.explaination);
      });*/
      block.append(jQuery('<div class="quiz-explaination"></div>'));
      this.showExplaination(question.explaination);
    },
    showExplaination: function(str) {
      var explaination = this.container.find('div.quiz-explaination');
      explaination.html(this.markItDown(str));
    },
    collectAnswers: function(ul) {
      var result = [];
      ul.find('input:checkbox:checked,input:radio:checked').each(function(index, elt){
        result.push(jQuery(elt).attr('value'));
      });
      return result;
    },
    addButton: function(text, callback) {
      var btn = jQuery('<span class="quiz-button">' + text + '</span>');
      this.container.append(btn);
      btn.on('click', callback);
    },
    getAnswer: function(id, answer) {
      var _self = this;
      _self.container.find('input:radio,input:checkbox').prop('disabled', true);
      Parse.Cloud.run('checkAnswers', {answers:[{questionId:id, answer:answer}]}, {
        success: function (result) {
          _self.container.html('');
          var correction = result.data[0];
          _self.buildAnswer(correction);
          _self.addButton('Rejouer', function(){
            _self.newQuestion();
          });
        },
        error: function (error) {
          console.log('The Quiz server is not reachable');
        }
      });
    }, setContainer: function(container) {
      this.container = container;
    },
    markItDown: function(str) {
      var converter = new Showdown.converter();
      return converter.makeHtml(str)
        .replace(/a href="\//g, 'a href="http://derby.parseapp.com/')
        .replace(/a href="/g, 'a target="_blank" href="');
    }
  };

  return QuizBonhommeObj;
})();