// Custom JavaScript for Sistema de Vendas e Estoque

$(document).ready(function() {
    // Initialize tooltips
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Initialize popovers
    var popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
    var popoverList = popoverTriggerList.map(function (popoverTriggerEl) {
        return new bootstrap.Popover(popoverTriggerEl);
    });

    // Auto-hide alerts after 5 seconds
    setTimeout(function() {
        $('.alert').fadeOut('slow');
    }, 5000);

    // Add fade-in animation to cards
    $('.card').addClass('fade-in');

    // Form validation
    $('form').on('submit', function(e) {
        var form = this;
        if (form.checkValidity() === false) {
            e.preventDefault();
            e.stopPropagation();
        }
        $(form).addClass('was-validated');
    });

    // Confirm delete actions
    $('[data-confirm]').on('click', function(e) {
        var message = $(this).data('confirm');
        if (!confirm(message)) {
            e.preventDefault();
        }
    });

    // Format currency inputs
    $('.currency-input').on('input', function() {
        var value = $(this).val().replace(/\D/g, '');
        value = (value / 100).toFixed(2);
        value = value.replace('.', ',');
        value = value.replace(/\B(?=(\d{3})+(?!\d))/g, '.');
        $(this).val('R$ ' + value);
    });

    // Format CPF/CNPJ inputs
    $('.cpf-cnpj-input').on('input', function() {
        var value = $(this).val().replace(/\D/g, '');
        if (value.length <= 11) {
            // CPF format
            value = value.replace(/(\d{3})(\d)/, '$1.$2');
            value = value.replace(/(\d{3})(\d)/, '$1.$2');
            value = value.replace(/(\d{3})(\d{1,2})$/, '$1-$2');
        } else {
            // CNPJ format
            value = value.replace(/^(\d{2})(\d)/, '$1.$2');
            value = value.replace(/^(\d{2})\.(\d{3})(\d)/, '$1.$2.$3');
            value = value.replace(/\.(\d{3})(\d)/, '.$1/$2');
            value = value.replace(/(\d{4})(\d)/, '$1-$2');
        }
        $(this).val(value);
    });

    // Format phone inputs
    $('.phone-input').on('input', function() {
        var value = $(this).val().replace(/\D/g, '');
        if (value.length <= 10) {
            value = value.replace(/(\d{2})(\d)/, '($1) $2');
            value = value.replace(/(\d{4})(\d)/, '$1-$2');
        } else {
            value = value.replace(/(\d{2})(\d)/, '($1) $2');
            value = value.replace(/(\d{5})(\d)/, '$1-$2');
        }
        $(this).val(value);
    });

    // Search functionality
    $('.search-input').on('keyup', function() {
        var value = $(this).val().toLowerCase();
        var target = $(this).data('target');
        $(target + ' tbody tr').filter(function() {
            $(this).toggle($(this).text().toLowerCase().indexOf(value) > -1);
        });
    });

    // Table row selection
    $('.table-selectable tbody tr').on('click', function() {
        $(this).toggleClass('table-active');
        var checkbox = $(this).find('input[type="checkbox"]');
        checkbox.prop('checked', !checkbox.prop('checked'));
    });

    // Bulk actions
    $('#select-all').on('change', function() {
        var checked = $(this).prop('checked');
        $('.table-selectable tbody input[type="checkbox"]').prop('checked', checked);
        $('.table-selectable tbody tr').toggleClass('table-active', checked);
    });

    // Dynamic form fields
    $('.add-field').on('click', function() {
        var template = $(this).data('template');
        var container = $(this).data('container');
        var newField = $(template).clone();
        newField.find('input, select, textarea').val('');
        $(container).append(newField);
    });

    $('.remove-field').on('click', function() {
        $(this).closest('.field-group').remove();
    });

    // Auto-save functionality
    $('.auto-save').on('change', function() {
        var form = $(this).closest('form');
        var url = form.attr('action');
        var data = form.serialize();
        
        $.ajax({
            url: url,
            method: 'POST',
            data: data,
            success: function(response) {
                showNotification('Dados salvos automaticamente', 'success');
            },
            error: function() {
                showNotification('Erro ao salvar dados', 'error');
            }
        });
    });

    // Notification system
    function showNotification(message, type) {
        var alertClass = 'alert-info';
        var icon = 'fa-info-circle';
        
        switch(type) {
            case 'success':
                alertClass = 'alert-success';
                icon = 'fa-check-circle';
                break;
            case 'error':
                alertClass = 'alert-danger';
                icon = 'fa-exclamation-circle';
                break;
            case 'warning':
                alertClass = 'alert-warning';
                icon = 'fa-exclamation-triangle';
                break;
        }
        
        var notification = $('<div class="alert ' + alertClass + ' alert-dismissible fade show" role="alert">' +
            '<i class="fas ' + icon + ' me-2"></i>' + message +
            '<button type="button" class="btn-close" data-bs-dismiss="alert"></button>' +
            '</div>');
        
        $('.container-fluid').prepend(notification);
        
        setTimeout(function() {
            notification.fadeOut('slow', function() {
                $(this).remove();
            });
        }, 3000);
    }

    // Loading states
    $('.btn-loading').on('click', function() {
        var btn = $(this);
        var originalText = btn.html();
        btn.html('<span class="spinner-border spinner-border-sm me-2" role="status"></span>Carregando...');
        btn.prop('disabled', true);
        
        setTimeout(function() {
            btn.html(originalText);
            btn.prop('disabled', false);
        }, 2000);
    });

    // Print functionality
    $('.btn-print').on('click', function() {
        window.print();
    });

    // Export functionality
    $('.btn-export').on('click', function() {
        var format = $(this).data('format');
        var table = $(this).data('table');
        
        if (format === 'csv') {
            exportTableToCSV(table);
        } else if (format === 'excel') {
            exportTableToExcel(table);
        }
    });

    function exportTableToCSV(tableId) {
        var csv = [];
        var rows = document.querySelectorAll(tableId + ' tr');
        
        for (var i = 0; i < rows.length; i++) {
            var row = [], cols = rows[i].querySelectorAll('td, th');
            
            for (var j = 0; j < cols.length; j++) {
                row.push(cols[j].innerText);
            }
            
            csv.push(row.join(','));
        }
        
        downloadCSV(csv.join('\n'), 'export.csv');
    }

    function downloadCSV(csv, filename) {
        var csvFile;
        var downloadLink;
        
        csvFile = new Blob([csv], {type: 'text/csv'});
        downloadLink = document.createElement('a');
        downloadLink.download = filename;
        downloadLink.href = window.URL.createObjectURL(csvFile);
        downloadLink.style.display = 'none';
        document.body.appendChild(downloadLink);
        downloadLink.click();
    }

    // Real-time search
    $('.real-time-search').on('keyup', function() {
        var query = $(this).val();
        var url = $(this).data('url');
        var target = $(this).data('target');
        
        if (query.length >= 3) {
            $.ajax({
                url: url,
                method: 'GET',
                data: { q: query },
                success: function(data) {
                    $(target).html(data);
                }
            });
        }
    });

    // Sidebar toggle
    $('.sidebar-toggle').on('click', function() {
        $('body').toggleClass('sidebar-collapsed');
    });

    // Theme toggle
    $('.theme-toggle').on('click', function() {
        $('body').toggleClass('dark-theme');
        localStorage.setItem('theme', $('body').hasClass('dark-theme') ? 'dark' : 'light');
    });

    // Load saved theme
    var savedTheme = localStorage.getItem('theme');
    if (savedTheme === 'dark') {
        $('body').addClass('dark-theme');
    }

    // Smooth scrolling
    $('a[href^="#"]').on('click', function(e) {
        e.preventDefault();
        var target = $(this.getAttribute('href'));
        if (target.length) {
            $('html, body').stop().animate({
                scrollTop: target.offset().top - 100
            }, 1000);
        }
    });

    // Back to top button
    $(window).scroll(function() {
        if ($(this).scrollTop() > 100) {
            $('.back-to-top').fadeIn();
        } else {
            $('.back-to-top').fadeOut();
        }
    });

    $('.back-to-top').on('click', function() {
        $('html, body').animate({scrollTop: 0}, 800);
        return false;
    });
});

// Utility functions
function formatCurrency(value) {
    return new Intl.NumberFormat('pt-BR', {
        style: 'currency',
        currency: 'BRL'
    }).format(value);
}

function formatDate(date) {
    return new Intl.DateTimeFormat('pt-BR').format(new Date(date));
}

function formatDateTime(date) {
    return new Intl.DateTimeFormat('pt-BR', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    }).format(new Date(date));
}

function validateCPF(cpf) {
    cpf = cpf.replace(/[^\d]+/g, '');
    if (cpf.length !== 11 || !!cpf.match(/(\d)\1{10}/)) return false;
    
    var sum = 0;
    var remainder;
    
    for (var i = 1; i <= 9; i++) {
        sum = sum + parseInt(cpf.substring(i-1, i)) * (11 - i);
    }
    
    remainder = (sum * 10) % 11;
    if ((remainder === 10) || (remainder === 11)) remainder = 0;
    if (remainder !== parseInt(cpf.substring(9, 10))) return false;
    
    sum = 0;
    for (var i = 1; i <= 10; i++) {
        sum = sum + parseInt(cpf.substring(i-1, i)) * (12 - i);
    }
    
    remainder = (sum * 10) % 11;
    if ((remainder === 10) || (remainder === 11)) remainder = 0;
    if (remainder !== parseInt(cpf.substring(10, 11))) return false;
    
    return true;
}

function validateCNPJ(cnpj) {
    cnpj = cnpj.replace(/[^\d]+/g, '');
    if (cnpj.length !== 14) return false;
    
    var length = cnpj.length - 2;
    var numbers = cnpj.substring(0, length);
    var digits = cnpj.substring(length);
    var sum = 0;
    var pos = length - 7;
    
    for (var i = length; i >= 1; i--) {
        sum += numbers.charAt(length - i) * pos--;
        if (pos < 2) pos = 9;
    }
    
    var result = sum % 11 < 2 ? 0 : 11 - sum % 11;
    if (result !== parseInt(digits.charAt(0))) return false;
    
    length = length + 1;
    numbers = cnpj.substring(0, length);
    sum = 0;
    pos = length - 7;
    
    for (var i = length; i >= 1; i--) {
        sum += numbers.charAt(length - i) * pos--;
        if (pos < 2) pos = 9;
    }
    
    result = sum % 11 < 2 ? 0 : 11 - sum % 11;
    if (result !== parseInt(digits.charAt(1))) return false;
    
    return true;
}

